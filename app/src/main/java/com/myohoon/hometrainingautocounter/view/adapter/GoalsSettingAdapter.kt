package com.myohoon.hometrainingautocounter.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.myohoon.hometrainingautocounter.databinding.ItemGoalsSettingBinding
import com.myohoon.hometrainingautocounter.repository.enums.GoalsSettingType

class GoalsSettingAdapter(
    private val obsList : ObservableField<List<GoalsSettingType>>
): RecyclerView.Adapter<GoalsSettingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGoalsSettingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        obsList.get()?.let { holder.bind(it[position]) }
    }


    override fun getItemCount(): Int {
        return obsList.get()?.size ?: 0
    }

    class ViewHolder(
        private val binding: ItemGoalsSettingBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object{
            const val TAG = "ViewHolder"
        }

        fun bind(item: GoalsSettingType){
            binding.item = item

            binding.clExerciseItem.setOnClickListener {

            }
            binding.ibInfo.setOnClickListener {

            }
        }
    }
}