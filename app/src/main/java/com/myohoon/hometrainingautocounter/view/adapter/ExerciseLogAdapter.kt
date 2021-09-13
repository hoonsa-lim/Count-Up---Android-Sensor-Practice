package com.myohoon.hometrainingautocounter.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.myohoon.hometrainingautocounter.databinding.ItemExerciseLogBinding
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseLogDetail
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseLogStart
import com.myohoon.hometrainingautocounter.utils.TimeUtils

class ExerciseLogAdapter(
    private val obsList : ObservableField<MutableList<ExerciseLogDetail>>,
): RecyclerView.Adapter<ExerciseLogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExerciseLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        obsList.get()?.let { holder.bind(it, position) }
    }


    override fun getItemCount(): Int {
        return obsList.get()?.size ?: 0
    }

    class ViewHolder(
        private val binding: ItemExerciseLogBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object{
            const val TAG = "ViewHolder"
        }

        fun bind(list: MutableList<ExerciseLogDetail>, position: Int){
            //순
            val item = list[position]
            binding.item = item
            binding.index = TimeUtils.timeDigitTwo(list.size - position, Int.MAX_VALUE)
        }
    }
}