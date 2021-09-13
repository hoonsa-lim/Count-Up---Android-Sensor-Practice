package com.myohoon.hometrainingautocounter.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_log_detail")
data class ExerciseLogDetail(
    @PrimaryKey(autoGenerate = true) var logDetailId: Int? = null,                //log detail id
    @ColumnInfo var logStartId: String,                //log id
    @ColumnInfo var count: Int?,                                      //횟수
    @ColumnInfo var timeSpent: Int,                                      //소요시간
    @ColumnInfo var status: String,                                      //상태
    @ColumnInfo var saveAt: String,                                      //저장된 시간
)
