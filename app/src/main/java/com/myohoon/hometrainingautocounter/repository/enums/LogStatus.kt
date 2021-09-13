package com.myohoon.hometrainingautocounter.repository.enums

enum class LogStatus {
    SUCCESS,                            //목표 성공
    FAIL_TIME_OVER,                     //목표 있지만 실패 - 시간 초과
    FAIL_EARLY_FINISH,                  //목표 있지만 실패 - 조기 종료
    NORMAL_MANUAL_FINISH                //목표 없음, 수동 세트 종료
}