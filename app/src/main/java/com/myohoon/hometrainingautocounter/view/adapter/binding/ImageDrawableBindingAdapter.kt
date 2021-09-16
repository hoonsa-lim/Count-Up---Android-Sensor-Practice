package com.myohoon.hometrainingautocounter.view.adapter.binding

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.myohoon.hometrainingautocounter.R


object ImageDrawableBindingAdapter {
    @BindingAdapter(value = ["imageDrawable"])
    @JvmStatic
    fun setDrawable(imageView : ImageView, drawable : Drawable){
        imageView.setImageDrawable(drawable)
    }
}