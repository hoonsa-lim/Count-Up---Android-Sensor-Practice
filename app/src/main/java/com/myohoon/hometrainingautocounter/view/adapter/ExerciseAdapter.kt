package com.myohoon.hometrainingautocounter.view.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.myohoon.hometrainingautocounter.R
import com.myohoon.hometrainingautocounter.databinding.ItemExerciseBinding
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseEntity
import com.myohoon.hometrainingautocounter.repository.model.MyAlert
import com.myohoon.hometrainingautocounter.utils.AlertUtils
import com.myohoon.hometrainingautocounter.utils.ResUtils
import com.myohoon.hometrainingautocounter.view.MainActivity
import com.myohoon.hometrainingautocounter.view.fragment.main.GoalsSettingFragment
import com.myohoon.hometrainingautocounter.viewmodel.ExerciseViewModel

class ExerciseAdapter(
    private val obsList : ObservableField<MutableList<ExerciseEntity>>,
    private val exerciseVM: ExerciseViewModel,
): RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, exerciseVM)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        obsList.get()?.let { holder.bind(it[position]) }
    }


    override fun getItemCount(): Int {
        return obsList.get()?.size ?: 0
    }

    class ViewHolder(
        private val binding: ItemExerciseBinding,
        private val vm: ExerciseViewModel
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object{
            const val TAG = "ViewHolder"
        }

        fun bind(item: ExerciseEntity){
            //순
            val position = this.adapterPosition +1
            binding.num = if (position <= 10) "0$position" else position.toString()

            //운동 제목
            binding.title = ResUtils.getResExerciseName(item.eId)

            //event
            binding.clExerciseItem.setOnClickListener {
                if (item.isShowExplanation != null && !item.isShowExplanation.toBoolean())
                    addPageFragment(item)
                else{
                    showAlertExerciseInfo(
                        makeExerciseAlert(it.context, item, {
                            addPageFragment(item)
                        },{
                            item.isShowExplanation = "false"
                            vm.updateIsShowExplanation(item)
                        },R.string.start),
                        it.context
                    )
                }
            }
            binding.ibExerciseInfo.setOnClickListener {
                showAlertExerciseInfo(
                    makeExerciseAlert(it.context, item, {},{}, isOneBtn = true),
                    it.context
                )
            }
        }

        private fun addPageFragment(e: ExerciseEntity) {
            vm.currentExercise.set(e)
            MainActivity.addFragmentInMain(GoalsSettingFragment(), GoalsSettingFragment.TAG)
        }

        private fun makeExerciseAlert(
            context: Context,
            exercise: ExerciseEntity,
            f1: () -> Unit, f2: () -> Unit,
            btnPositiveText: Int? = null,
            isOneBtn: Boolean = false
        ): MyAlert {

             val alert = MyAlert(
                context.getString(ResUtils.getResExerciseName(exercise.eId)),
                context.getString(ResUtils.getResExerciseExplanation(exercise.eId)),
                 ResUtils.getResExerciseImg(exercise.eId),
                isOneButton = isOneBtn,
                btnPositiveText = if (btnPositiveText != null) context.getString(btnPositiveText) else null,
                btnNegativeText = context.getString(R.string.dont_show_again),
                positiveEvent = f1, negativeEvent = f2
            )
            return alert
        }

        private fun showAlertExerciseInfo(alert: MyAlert, context: Context){
            AlertUtils.instance().show(context, alert)
        }
    }
}