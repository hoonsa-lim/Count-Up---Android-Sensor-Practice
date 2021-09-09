package com.myohoon.hometrainingautocounter.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ObservableField
import com.myohoon.hometrainingautocounter.R
import com.myohoon.hometrainingautocounter.databinding.AlertCustomBinding
import com.myohoon.hometrainingautocounter.databinding.AlertGoalSettingBinding
import com.myohoon.hometrainingautocounter.databinding.AlertRestBinding
import com.myohoon.hometrainingautocounter.repository.AppShared
import com.myohoon.hometrainingautocounter.repository.entity.Goal
import com.myohoon.hometrainingautocounter.repository.enums.GoalsSettingType
import com.myohoon.hometrainingautocounter.repository.model.MyAlert
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

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

    fun showGoalSetting(context: Context?, goal: Goal, save:(count:String)-> Unit){
        val dl = context?.let { Dialog(it) }
        dl?.let { dialog->
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  //둥근 배경 적용을 위해 사용함

            val binding = AlertGoalSettingBinding.inflate(LayoutInflater.from(context))

            //title
            binding.tvGoalSettingTitle.setText(ResUtils.getGoalName(goal.goalId))

            //num
            val count = ObservableField(
                when(goal.goalId.split("_").last().toInt()) {
                    GoalsSettingType.TIME_LIMIT_PER_SET.ordinal,
                    GoalsSettingType.TIME_REST.ordinal -> TimeUtils.secToFormatTime(goal.lastGoalsValue.toInt())
                    else -> goal.lastGoalsValue
                }
            )
            binding.count = count

            //calculate
            val id = goal.goalId.split("_").last().toInt()
            if (id == GoalsSettingType.SETS.ordinal || id == GoalsSettingType.REPS.ordinal){
                binding.btnA1000.visibility = View.GONE
                binding.btnM1000.visibility = View.GONE
            }else{
                val unitSec = context.getString(R.string.sec)
                val unitMinute = context.getString(R.string.min)
                binding.btnM1.text = "-1 $unitSec"
                binding.btnM10.text = "-10 $unitSec"
                binding.btnM100.text = "-1 $unitMinute"
                binding.btnM1000.text = "-10 $unitMinute"
                binding.btnA1.text = "+1 $unitSec"
                binding.btnA10.text = "+10 $unitSec"
                binding.btnA100.text = "+1 $unitMinute"
                binding.btnA1000.text = "+10 $unitMinute"
            }

            binding.btnM1.setOnClickListener { calculate(goal.goalId, -1, count) }
            binding.btnM10.setOnClickListener { calculate(goal.goalId, -10, count) }
            binding.btnM100.setOnClickListener { calculate(goal.goalId, -100, count) }
            binding.btnM1000.setOnClickListener { calculate(goal.goalId, -1000, count) }
            binding.btnA1.setOnClickListener { calculate(goal.goalId, 1, count) }
            binding.btnA10.setOnClickListener { calculate(goal.goalId, 10, count) }
            binding.btnA100.setOnClickListener { calculate(goal.goalId, 100, count) }
            binding.btnA1000.setOnClickListener { calculate(goal.goalId, 1000, count) }

            //cancel, save
            binding.btnNegative.setOnClickListener { dialog.dismiss() }
            binding.btnPositive.setOnClickListener {
                save(count.get()?:"")
                dialog.dismiss()
            }
            dialog.setContentView(binding.root)
            dialog.show()
        }
    }

    private fun calculate(id: String, i: Int, count: ObservableField<String>) {
        when(id.split("_").last().toInt()){
            GoalsSettingType.SETS.ordinal,
            GoalsSettingType.REPS.ordinal -> {   //숫자
                count.set("${(count.get()?.toInt()?:0) + i}")
            }
            GoalsSettingType.TIME_LIMIT_PER_SET.ordinal,
            GoalsSettingType.TIME_REST.ordinal-> {   //시간

                var now = TimeUtils.formatTimeToSec(count.get() ?: GoalsSettingType.DEFAULT_VALUE)

                if (i > 0){
                    //+
                    if (i == 1) now += 1
                    else if(i == 10) now += 10
                    else if(i == 100) now += 60
                    else if(i == 1000) now += 600
                }else{
                    //-
                    if (i == -1) now -= 1
                    else if(i == -10) now -= 10
                    else if(i == -100) now -= 60
                    else if(i == -1000) now -= 600
                }

                count.set(TimeUtils.secToFormatTime(now))
            }
            else -> return
        }

    }

    fun showRestTimer(c: Context?, time:Int){
        c?.let { Dialog(it)?.let { dialog ->
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  //둥근 배경 적용을 위해 사용함

            val binding = AlertRestBinding.inflate(LayoutInflater.from(c))
            var disposable: Disposable? = null

            //count
            binding.tvCount.text = TimeUtils.secToFormatTime(time)

            //check
            binding.switchAutoEnd.isChecked = AppShared.getInstance(c).isAutoEndRestTime

            //progress
            binding.progressBar.progress = 0
            binding.progressBar.max = time

            //event
            binding.btnEnd.setOnClickListener {
                disposable?.let { it.dispose() }
                AppShared.getInstance(c).isAutoEndRestTime = binding.switchAutoEnd.isChecked
                dialog.dismiss()
            }

            //timer
            disposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                   if (it+1 > time && binding.switchAutoEnd.isChecked)
                       binding.btnEnd.callOnClick()
                   else if(it+1 > time)
                       disposable?.dispose()
                   else{
                       binding.progressBar.progress = it.toInt()+1
                       binding.tvCount.text = TimeUtils.secToFormatTime((time - (it.toInt()+1)))
                   }

                },{
                    disposable?.dispose()
                })

            dialog.setContentView(binding.root)
            dialog.show()
        } }
    }
}