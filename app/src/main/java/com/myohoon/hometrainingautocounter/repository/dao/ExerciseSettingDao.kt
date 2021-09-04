package com.myohoon.hometrainingautocounter.repository.dao

import androidx.room.*
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseSetting
import io.reactivex.Observable

@Dao
interface ExerciseSettingDao {
    @Query("SELECT * FROM exercise_setting WHERE eId IN (:eId) LIMIT 1")
    fun selectByExerciseId(eId: Int): Observable<ExerciseSetting>

    @Insert
    fun insert(data: ExerciseSetting)

    @Delete
    fun delete(data: ExerciseSetting)
}