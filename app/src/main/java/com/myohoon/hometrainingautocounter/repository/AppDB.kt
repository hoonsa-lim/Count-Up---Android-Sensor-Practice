package com.myohoon.hometrainingautocounter.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.myohoon.hometrainingautocounter.repository.dao.ExerciseDao
import com.myohoon.hometrainingautocounter.repository.dao.GoalsSettingDao
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseEntity
import com.myohoon.hometrainingautocounter.repository.entity.Goal
import com.myohoon.hometrainingautocounter.repository.enums.ExerciseType
import com.myohoon.hometrainingautocounter.repository.enums.GoalsSettingType

@Database(entities = [ExerciseEntity::class, Goal::class], version = 1)
abstract class AppDB : RoomDatabase() {
    abstract fun exerciseSettingDao(): ExerciseDao
    abstract fun goalsSettingDao(): GoalsSettingDao

    companion object{
        private var appDB: AppDB? = null
        val exercises = enumValues<ExerciseType>()
        val goals = enumValues<GoalsSettingType>()

        fun instance(context: Context, uId: String): AppDB{
            if (appDB == null) appDB = Room
                .databaseBuilder(context.applicationContext, AppDB::class.java, "$uId.db")
                .addCallback(object: RoomDatabase.Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        //exercise 테이블 초기값
                        exercises.forEachIndexed { i, v ->
                            db.execSQL("insert into exercise values ($i, '${v.title}', 'true');")

                            //goals 테이블 초기값
                            goals.forEachIndexed { j, v1 ->
                                db.execSQL("insert into goals_setting values ('${i}_${j}', $i, 0, '0');")
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
            return appDB!!
        }
    }
}