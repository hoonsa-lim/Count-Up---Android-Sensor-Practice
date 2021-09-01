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
                showAlertExerciseInfo(item, it.context)
            }
            binding.ibExerciseInfo.setOnClickListener {
                showAlertExerciseInfo(item, it.context)
            }
        }

        private fun showAlertExerciseInfo(exercise: Exercise, context: Context){
            val alert = MyAlert(
                context.getString(exercise.title),
                context.getString(exercise.desc),
                R.drawable.test,
                isOneButton = false,
                btnPositiveText = "안녕",
                btnNegativeText = "반가워",
                {Toast.makeText(context, "1111", Toast.LENGTH_SHORT).show()},
                {Toast.makeText(context, "22222", Toast.LENGTH_SHORT).show()}
            )
            AlertUtils.instance().show(context, alert)
        }
    }
}