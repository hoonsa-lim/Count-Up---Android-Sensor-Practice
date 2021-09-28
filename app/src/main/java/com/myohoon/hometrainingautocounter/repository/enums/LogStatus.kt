package com.myohoon.hometrainingautocounter.repository.enums

import com.myohoon.hometrainingautocounter.R
import kotlin.Result.Companion.success

enum class LogStatus(val strRes:Int) {
    SUCCESS(R.string.success),                            //목표 성공
    TIME_OVER(R.string.time_over),                     //목표 있지만 실패 - 시간 초과
    EARLY_FINISH(R.string.early_finish),                  //목표 있지만 실패 - 조기 종료
    MANUALLY_FINISH(R.string.manually_finish)                //목표 없음, 수동 세트 종료
}