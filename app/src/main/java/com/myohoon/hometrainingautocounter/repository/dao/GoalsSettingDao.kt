package com.myohoon.hometrainingautocounter.repository.dao

import androidx.room.*
import com.myohoon.hometrainingautocounter.repository.entity.Goal
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
interface GoalsSettingDao {
    @Query("SELECT * FROM goals_setting WHERE eId IN (:eId) AND goalId IN (:gId) LIMIT 1")
    fun selectGoal(eId: Int, gId: Int): Observable<Goal>

    @Query("SELECT * FROM goals_setting")
    fun selectGoals(): Observable<MutableList<Goal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: Goal): Completable

    @Delete
    fun delete(data: Goal): Completable
}