package com.myohoon.hometrainingautocounter.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import androidx.room.PrimaryKey
import com.myohoon.hometrainingautocounter.R
import com.myohoon.hometrainingautocounter.databinding.ItemGoalsSettingBinding
import com.myohoon.hometrainingautocounter.repository.entity.Goal
import com.myohoon.hometrainingautocounter.repository.enums.GoalsSettingType
import com.myohoon.hometrainingautocounter.utils.AlertUtils
import com.myohoon.hometrainingautocounter.utils.ResUtils
import com.myohoon.hometrainingautocounter.utils.TimeUtils
import com.myohoon.hometrainingautocounter.viewmodel.ExerciseViewModel

class GoalsSettingAdapter(
    private val list : ObservableField<MutableList<Goal>>,
    private val exerciseVM: ExerciseViewModel
): RecyclerView.Adapter<GoalsSettingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGoalsSettingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, exerciseVM)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list.get()?.let { holder.bind(it[position]) }
    }

    override fun getItemCount(): Int = if (list.get() == null) 0 else list.get()!!.size

    class ViewHolder(
        private val binding: ItemGoalsSettingBinding,
        private val exerciseVM: ExerciseViewModel
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object{
            const val TAG = "ViewHolder"
        }

        fun bind(item: Goal){
            val context = binding.root.context

            //활성화 상태
            binding.clExerciseItem.alpha = if (item.isActive) 1.0f else 0.4f
            binding.cbItemActive.isChecked = item.isActive

            //제목
            binding.tvItemTitle.text = context.getString(ResUtils.getGoalName(item.goalId))

            //값
            binding.tvItemNum.text = "${preprocess(item)}${getUnit(item.goalId, context)}"

            //event
            binding.cbItemActive.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP){
                    item.isActive = !item.isActive
                    exerciseVM.updateGoal(item)
                }
                true
            }
            binding.clExerciseItem.setOnClickListener {
                if (!item.isActive){
                    item.isActive = true
                    exerciseVM.updateGoal(item)
                }
            }
            binding.tvItemNum.setOnClickListener {
                if (item.isActive){
                    AlertUtils.instance().showGoalSetting(it.context, item) {
                        updateCount(it, item, exerciseVM)
                    }
                }
            }
            binding.ibInfo.setOnClickListener {

            }
        }
        private fun preprocess(item: Goal): String{
            return when(item.goalId.split("_").last().toInt()){
                GoalsSettingType.SETS.ordinal,
                GoalsSettingType.REPS.ordinal -> item.lastGoalsValue

                GoalsSettingType.TIME_LIMIT_PER_SET.ordinal,
                GoalsSettingType.TIME_REST.ordinal ->
                    if (item.lastGoalsValue == "0") "00:00"
                    else TimeUtils.secToFormatTime(item.lastGoalsValue.toInt())
                else -> ""
            }
        }

        private fun getUnit(goalId: String, context: Context): String {
            return when(goalId.split("_").last().toInt()){
                GoalsSettingType.SETS.ordinal -> " ${context.getString(R.string.unit_set)}"
                GoalsSettingType.REPS.ordinal -> " ${context.getString(R.string.unit_count)}"
                else -> ""
            }
        }

        private fun updateCount(count:String, item:Goal, exerciseVM: ExerciseViewModel){
            when(item.goalId.split("_").last().toInt()){
                GoalsSettingType.SETS.ordinal,
                GoalsSettingType.REPS.ordinal -> {   //숫자
                    if ((count.toInt()?:0) < 0) return
                    item.lastGoalsValue = count
                    exerciseVM.updateGoal(item)
                }
                GoalsSettingType.TIME_LIMIT_PER_SET.ordinal,
                GoalsSettingType.TIME_REST.ordinal -> {       //시간
                    item.lastGoalsValue = TimeUtils.formatTimeToSec(count).toString()
                    exerciseVM.updateGoal(item)
                }
            }
        }
    }
}