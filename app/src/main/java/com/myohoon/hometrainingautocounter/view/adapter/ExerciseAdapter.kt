package com.myohoon.hometrainingautocounter.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.myohoon.hometrainingautocounter.R
import com.myohoon.hometrainingautocounter.databinding.ItemExerciseBinding
import com.myohoon.hometrainingautocounter.repository.enums.Exercise
import com.myohoon.hometrainingautocounter.repository.model.MyAlert
import com.myohoon.hometrainingautocounter.utils.AlertUtils
import com.myohoon.hometrainingautocounter.view.MainActivity
import com.myohoon.hometrainingautocounter.view.fragment.main.GoalsSettingFragment

class ExerciseAdapter(
    private val obsList : ObservableField<List<Exercise>>
): RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        obsList.get()?.let { holder.bind(it[position]) }
    }


    override fun getItemCount(): Int {
        return obsList.get()?.size ?: 0
    }

    class ViewHolder(
        private val binding: ItemExerciseBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object{
            const val TAG = "ViewHolder"
        }

        fun bind(item: Exercise){
            val position = this.adapterPosition +1
            binding.item = item
            binding.num = if (position <= 10) "0$position" else position.toString()

            binding.clExerciseItem.setOnClickListener {
                if (false)
                    addPageFragment()
                else{
                    showAlertExerciseInfo(
                        makeExerciseAlert(it.context, item, {
                            addPageFragment()
                        },{
                            Toast.makeText(it.context, "다음에 보지 않기", Toast.LENGTH_SHORT).show()
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

        private fun addPageFragment() {
            MainActivity.addFragmentInMain(GoalsSettingFragment(), GoalsSettingFragment.TAG)
        }

        private fun makeExerciseAlert(
            context: Context,
            exercise: Exercise,
            f1: () -> Unit, f2: () -> Unit,
            btnPositiveText: Int? = null,
            isOneBtn: Boolean = false
        ): MyAlert {

             val alert = MyAlert(
                context.getString(exercise.title),
                context.getString(exercise.desc),
                exercise.img,
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