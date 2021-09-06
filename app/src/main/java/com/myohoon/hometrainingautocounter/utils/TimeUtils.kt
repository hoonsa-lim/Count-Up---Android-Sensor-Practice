package com.myohoon.hometrainingautocounter.utils

import android.os.Build
import android.util.Log
import android.util.TimeUtils
import java.lang.Exception
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class TimeUtils {
    companion object{
        const val TAG = "TimeUtils"
        const val DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss"
        const val TIME_PATTERN = "mm:ss"

        fun timeDigitTwo(int: Int, max:Int): String{
            return when(int){
                in 0..9 -> "0$int"
                in 10..max -> "$int"
                else -> "$max"
            }
        }

        fun formatTimeToSec(time:String):Int{
            if (time.contains(":")){
                val mmss = time.split(":")
                return (mmss.first().toInt() * 60) + mmss.last().toInt()
            }else{
                return time.toInt()
            }
        }

        fun secToFormatTime(sec: Int):String{
            var v = sec
            if (v < 0) v = 0

            var m = 0
            var s = 0

            if (v >= 60) {
                m = v/60
                s = v%60
            }else{
                s = v
            }

            return "${timeDigitTwo(m, 60)}:${timeDigitTwo(s, 59)}"
        }

        fun getUnixTime(): Int{
            return (System.currentTimeMillis() / 1000).toInt()
        }

//        fun convertUnixToFormat(second:Long): String{
//            try {
//                val date =
//                    Instant.ofEpochSecond(second).atZone(ZoneId.systemDefault()).toLocalDateTime()
//                return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
//            }catch (e: Exception){
//                return ""
//            }
//        }

        fun currentFormatTime(): String{
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATETIME_PATTERN))
            } else {
                val sdf = SimpleDateFormat(DATETIME_PATTERN)
                sdf.timeZone = TimeZone.getDefault()
                sdf.format(Date())
            }
        }
    }

//    fun getTimeArrayStep30Minute(): ArrayList<String> {
//        val timeArray = ArrayList<String>()
//        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd (E) HH:mm")
//        var today = LocalDateTime.now()
//
//        // 현재 시간 저장
//        timeArray.add(today.format(dateFormatter))
//
//        // 이후 시간 생성
//        for (i in 0 until 48){
//            today = today.withMinute(if (today.minute < 30) 0 else 30)
//            today = today.plusMinutes(30)
//            timeArray.add(today.format(dateFormatter))
//            Log.d("TAG", "getTimeArrayStep30Minute: ${timeArray.get(i)} ${timeArray.size}")
//        }
//
//        return timeArray
//    }
//
//    //format //2021-07-16 00:00
//    fun convertTime(time:String): String{
//        if (time.split("/").size < 2) return@convertTime ""
//
//        val t = time.split(" ")
//        val monthAndDay = t.first().split("/")
//        val m = monthAndDay.first()
//        val d = monthAndDay.last()
//        val time = t.last()
//
//        return "${LocalDateTime.now().year}-$m-$d $time"
//    }
}