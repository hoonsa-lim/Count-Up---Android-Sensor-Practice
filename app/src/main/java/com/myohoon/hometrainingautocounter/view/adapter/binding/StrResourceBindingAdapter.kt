package com.myohoon.hometrainingautocounter.view.adapter.binding

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.myohoon.hometrainingautocounter.R


object StrResourceBindingAdapter {
    @BindingAdapter(value = ["strRes"])
    @JvmStatic
    fun setTextResource(tv : TextView, resId : Int){
        tv.setText(resId)
    }

    @BindingAdapter(value = ["strRes"])
    @JvmStatic
    fun setTextResource(tv : Button, resId : Int){
        tv.setText(resId)
    }
}