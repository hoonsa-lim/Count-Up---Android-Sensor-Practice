package com.myohoon.hometrainingautocounter.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_setting")
data class ExerciseSetting(
    @PrimaryKey val eId: Int,                                                               //운동 id
    @ColumnInfo(name = "is_show_explanation") val isShowExplanation: Boolean?,              //운동 설명 다이얼로그 dont show again
)
