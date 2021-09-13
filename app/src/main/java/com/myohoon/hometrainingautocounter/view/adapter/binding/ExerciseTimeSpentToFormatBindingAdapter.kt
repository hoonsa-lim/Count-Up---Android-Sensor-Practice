package com.myohoon.hometrainingautocounter.view.adapter.binding

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.myohoon.hometrainingautocounter.R
import com.myohoon.hometrainingautocounter.utils.ResUtils
import com.myohoon.hometrainingautocounter.utils.TimeUtils


object ExerciseTimeSpentToFormatBindingAdapter {
    @BindingAdapter(value = ["exerciseTimeSpent"])
    @JvmStatic
    fun setExerciseTimeSpent(tv : TextView, time : Int){
        tv.text = TimeUtils.secToFormatTime(time)
    }
}