package com.myohoon.hometrainingautocounter.utils

import com.myohoon.hometrainingautocounter.R

class ResUtils {
    companion object{
        //운동 명
        @JvmStatic
        fun getResExerciseName(eId: Int): Int {
            return when(eId){
                0 -> R.string.exercise_push_up
                1 -> R.string.exercise_squat
                2 -> R.string.exercise_chin_up
                3 -> R.string.exercise_sit_up
                4 -> R.string.exercise_plank
                else -> R.string.unknown
            }
        }

        //운동 설명
        fun getResExerciseImg(eId: Int): Int {
            return when(eId){
                0 -> R.drawable.test
                1 -> R.drawable.test
                2 -> R.drawable.test
                3 -> R.drawable.test
                4 -> R.drawable.test
                else -> R.string.unknown
            }
        }

        //운동 설명
        fun getResExerciseExplanation(eId: Int): Int{
            return when(eId){
                0 -> R.string.exercise_desc_push_up
                1 -> R.string.exercise_desc_squat
                2 -> R.string.exercise_desc_chin_up
                3 -> R.string.exercise_desc_sit_up
                4 -> R.string.exercise_desc_plank
                else -> R.string.unknown
            }
        }

        //목표 - 제목
        fun getGoalName(goalId:String): Int{
            return when(goalId.split("_").last()){
                "0" -> R.string.sets
                "1" -> R.string.reps
                "2" -> R.string.time_limit_per_set
                "3" -> R.string.time_rest
                else -> R.string.unknown
            }
        }
    }
}