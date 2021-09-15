package com.myohoon.hometrainingautocounter.repository.enums

enum class LogStatus {
    SUCCESS,                            //목표 성공
    TIME_OVER,                     //목표 있지만 실패 - 시간 초과
    EARLY_FINISH,                  //목표 있지만 실패 - 조기 종료
    MANUALLY_FINISH                //목표 없음, 수동 세트 종료
}