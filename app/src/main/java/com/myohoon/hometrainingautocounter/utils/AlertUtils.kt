package com.myohoon.hometrainingautocounter.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.myohoon.hometrainingautocounter.databinding.AlertCustomBinding
import com.myohoon.hometrainingautocounter.repository.model.MyAlert

class AlertUtils {
    companion object{
        const val TAG = "AlertUtils"

        private var alertUtils: AlertUtils? = null

        fun instance(): AlertUtils{
            if (alertUtils == null) alertUtils = AlertUtils()
            return alertUtils!!
        }
    }

    fun show(context: Context?, alert:MyAlert){
        context?.let { Dialog(it)?.let { dialog ->

        } }
        val dl = context?.let { Dialog(it) }
        dl?.let { dialog->
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  //둥근 배경 적용을 위해 사용함

            val binding = AlertCustomBinding.inflate(LayoutInflater.from(context))
            binding.myAlert = alert

            //event
            binding.btnNegative.setOnClickListener {
                alert.negativeEvent()
                dialog.dismiss()
            }
            binding.btnPositive.setOnClickListener {
                alert.positiveEvent()
                dialog.dismiss()
            }
            dialog.setContentView(binding.root)
            dialog.show()
        }
    }
}