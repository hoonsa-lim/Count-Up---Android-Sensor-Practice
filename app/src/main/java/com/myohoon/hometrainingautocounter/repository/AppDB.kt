package com.myohoon.hometrainingautocounter.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.myohoon.hometrainingautocounter.repository.dao.ExerciseSettingDao
import com.myohoon.hometrainingautocounter.repository.dao.GoalsSettingDao
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseSetting
import com.myohoon.hometrainingautocounter.repository.entity.GoalsSetting

@Database(entities = [ExerciseSetting::class, GoalsSetting::class], version = 1)
abstract class AppDB : RoomDatabase() {
    abstract fun exerciseSettingDao(): ExerciseSettingDao
    abstract fun goalsSettingDao(): GoalsSettingDao

    companion object{
        private var appDB: AppDB? = null

        fun instance(context: Context, uId: String): AppDB{
            if (appDB == null) Room
                .databaseBuilder(context.applicationContext, AppDB::class.java, "uId.db")
                .fallbackToDestructiveMigration()
                .build()
            return appDB!!
        }
    }
}