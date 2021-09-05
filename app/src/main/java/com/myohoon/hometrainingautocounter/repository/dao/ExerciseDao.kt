package com.myohoon.hometrainingautocounter.repository.dao

import androidx.room.*
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseEntity
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise")
    fun selectAll(): Observable<MutableList<ExerciseEntity>>

    @Insert
    fun insert(data: ExerciseEntity): Completable

    @Query("UPDATE exercise SET is_show_explanation = :isShow WHERE eId = :eId")
    fun update(isShow:String, eId:Int): Completable

    @Delete
    fun delete(data: ExerciseEntity): Completable
}