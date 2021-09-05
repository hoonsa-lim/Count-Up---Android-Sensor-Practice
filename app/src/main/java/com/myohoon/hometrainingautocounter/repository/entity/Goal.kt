package com.myohoon.hometrainingautocounter.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals_setting")
data class Goal(
    @PrimaryKey var goalId: String,                                                    //pk   ex) eId_goalID
    @ColumnInfo var eId: Int,                                                         //운동 id
    @ColumnInfo var isActive: Boolean,                                               //체크박스 여부   //정수 true==1, false==0
    @ColumnInfo var lastGoalsValue: String ,                                         //마지막 설정한 세트수, 회수, 시간, 후식시간 등
)
