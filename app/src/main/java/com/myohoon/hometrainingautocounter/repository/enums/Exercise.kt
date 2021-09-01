package com.myohoon.hometrainingautocounter.repository.enums

import com.myohoon.hometrainingautocounter.R

enum class Exercise(val title:Int, val desc:Int) {
    PUSH_UP(R.string.exercise_push_up, R.string.exercise_desc_push_up),
    SQUAT(R.string.exercise_squat, R.string.exercise_desc_squat),
    CHIN_UP(R.string.exercise_chin_up, R.string.exercise_desc_chin_up),
    SIT_UP(R.string.exercise_sit_up, R.string.exercise_desc_sit_up),
    PLANK(R.string.exercise_plank, R.string.exercise_desc_plank),
}