package com.myohoon.hometrainingautocounter.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_log_end")
data class ExerciseLogEnd(
    @PrimaryKey(autoGenerate = false) var logId: Int,                //log id
    @ColumnInfo var eId: Int,                                      //exercise id
    @ColumnInfo var date: String,                                      //2021-09-13
    @ColumnInfo var time: String,                                      // 20:00
)
