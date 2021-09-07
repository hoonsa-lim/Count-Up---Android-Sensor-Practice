package com.myohoon.hometrainingautocounter.repository.enums

enum class GoalsSettingType(val title: String,) {
    SETS("Sets"),                                       //세트 수
    REPS("Reps"),                                       //회 수       //영어 속어 repeat
    TIME_LIMIT_PER_SET("TimeLimitPerSet"),              //time limit per 1 set
    TIME_REST("TimeRest");
}