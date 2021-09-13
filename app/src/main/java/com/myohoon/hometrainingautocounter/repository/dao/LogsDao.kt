package com.myohoon.hometrainingautocounter.repository.dao

import androidx.room.*
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseLogDetail
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseLogStart
import com.myohoon.hometrainingautocounter.repository.entity.Goal
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
interface LogsDao {
    @Query("SELECT * FROM exercise_log_start")
    fun selectExerciseStartAll(): Observable<MutableList<ExerciseLogStart>>

    @Query("SELECT * FROM exercise_log_detail WHERE logStartId = :logStartId ORDER BY saveAt DESC")
    fun selectLogDetailsByLogStartId(logStartId:String): Observable<MutableList<ExerciseLogDetail>>

    @Insert
    fun insertLogStart(data: ExerciseLogStart): Completable

    @Insert
    fun insertLogDetail(data: ExerciseLogDetail): Completable
}