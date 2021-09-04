package com.myohoon.hometrainingautocounter.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals_setting")
data class GoalsSetting(
    @PrimaryKey val eId: Int,                                                           //운동 id
    @PrimaryKey val goalsId: Int,                                                       //목표 id
    @ColumnInfo val isActive: Boolean?,                                                 //체크박스 여부
    @ColumnInfo val lastGoalsValue: String? ,                                           //마지막 설정한 세트수
)
