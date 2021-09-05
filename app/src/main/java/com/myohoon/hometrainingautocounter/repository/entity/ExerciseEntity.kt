package com.myohoon.hometrainingautocounter.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise")
data class ExerciseEntity(
    @PrimaryKey var eId: Int,                                                               //운동 id
    @ColumnInfo(name = "e_name") var eName: String,                                       //운동명
    @ColumnInfo(name = "is_show_explanation") var isShowExplanation: String,             //운동 설명 다이얼로그 dont show again
)
