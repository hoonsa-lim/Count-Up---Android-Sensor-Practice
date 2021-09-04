package com.myohoon.hometrainingautocounter.repository.enums

import com.myohoon.hometrainingautocounter.R

enum class GoalsSettingType(val title: Int,) {
    SETS(R.string.sets),                       //세트 수
    REPS(R.string.reps),                       //회 수       //영어 속어 repeat
    TIME_LIMIT_PER_SET(R.string.time_limit_per_set),         //time limit per 1 set
    TIME_REST(R.string.time_rest);
}