package com.myohoon.hometrainingautocounter.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.myohoon.hometrainingautocounter.R
import com.myohoon.hometrainingautocounter.databinding.ItemExerciseBinding
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseEntity
import com.myohoon.hometrainingautocounter.repository.enums.ExerciseType
import com.myohoon.hometrainingautocounter.repository.model.Alert
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

            //팔굽혀펴기 외 준비중 처리
            if (this.adapterPosition == ExerciseType.PUSH_UP.ordinal){
                binding.clExerciseItem.alpha = 1.0f
            }

            //운동 제목
            binding.title = ResUtils.getResExerciseName(item.eId)

            //event
            binding.clExerciseItem.setOnClickListener {
                //팔굽혀펴기 외 준비중 처리
                if (this.adapterPosition != ExerciseType.PUSH_UP.ordinal){
                    Toast.makeText(it.context, R.string.services_preparing, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (item.isShowExplanation != null && !item.isShowExplanation.toBoolean())
                    addPageFragment(item)
                else{
                    showAlertExerciseInfo(
                        makeExerciseAlert(item, {
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
                //팔굽혀펴기 외 준비중 처리
                if (this.adapterPosition != ExerciseType.PUSH_UP.ordinal){
                    Toast.makeText(it.context, R.string.services_preparing, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                showAlertExerciseInfo(makeExerciseAlert(item, isOneBtn = true), it.context)
            }
        }

        private fun addPageFragment(e: ExerciseEntity) {
            vm.currentExercise.set(e)
            MainActivity.changeFragmentInMain(GoalsSettingFragment(), GoalsSettingFragment.TAG)
        }

        private fun makeExerciseAlert(
            exercise: ExerciseEntity,
            f1: (() -> Unit)? = null, f2: (() -> Unit)? = null,
            btnPositiveText: Int? = null,
            isOneBtn: Boolean = false
        ): Alert {
             val alert = Alert(
                 ResUtils.getResExerciseName(exercise.eId),
                 ResUtils.getResExerciseExplanation(exercise.eId),
//                 ResUtils.getResExerciseImg(exercise.eId),
                isOneButton = isOneBtn,
                btnNegativeText = R.string.dont_show_again,
                positiveEvent = f1, negativeEvent = f2,
            )
            return alert
        }

        private fun showAlertExerciseInfo(alert: Alert, context: Context){
            AlertUtils.instance().show(context, alert)
        }
    }
}