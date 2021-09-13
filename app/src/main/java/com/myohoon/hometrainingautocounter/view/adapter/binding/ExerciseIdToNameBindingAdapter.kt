package com.myohoon.hometrainingautocounter.view.adapter.binding

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.myohoon.hometrainingautocounter.R
import com.myohoon.hometrainingautocounter.utils.ResUtils


object ExerciseIdToNameBindingAdapter {
    @BindingAdapter(value = ["exerciseIdToName"])
    @JvmStatic
    fun setExerciseNameById(tv : TextView, exerciseId : Int){
        tv.setText(ResUtils.getResExerciseName(exerciseId))
    }
}