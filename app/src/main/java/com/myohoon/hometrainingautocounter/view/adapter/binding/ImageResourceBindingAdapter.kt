package com.myohoon.hometrainingautocounter.view.adapter.binding

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.myohoon.hometrainingautocounter.R


object ImageResourceBindingAdapter {
    @BindingAdapter(value = ["imageRes"])
    @JvmStatic
    fun loadImage(imageView : ImageView, resId : Int){
        Glide.with(imageView.context)
            .load(resId)
            .placeholder(R.color.gray_100)
            .error(R.color.gray_100)
            .into(imageView)
    }
}

object StrResourceBindingAdapter {
    @BindingAdapter(value = ["strRes"])
    @JvmStatic
    fun loadImage(tv : TextView, resId : Int){
        tv.setText(resId)
    }
}