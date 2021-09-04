package com.myohoon.hometrainingautocounter.repository.dao

import androidx.room.*
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseSetting
import com.myohoon.hometrainingautocounter.repository.entity.GoalsSetting
import io.reactivex.Observable

@Dao
interface GoalsSettingDao {
    @Query("SELECT * FROM goals_setting WHERE eId IN (:eId) AND goalsId IN (:gId) LIMIT 1")
    fun selectGoal(eId: Int, gId: Int): Observable<GoalsSetting>

    @Query("SELECT * FROM goals_setting WHERE eId IN (:eId)")
    fun selectGoalsByExerciseId(eId: Int): Observable<List<GoalsSetting>>

    @Insert
    fun insert(data: GoalsSetting)

    @Delete
    fun delete(data: GoalsSetting)
}