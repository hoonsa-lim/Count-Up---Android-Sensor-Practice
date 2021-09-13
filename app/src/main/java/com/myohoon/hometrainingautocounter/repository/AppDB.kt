package com.myohoon.hometrainingautocounter.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.myohoon.hometrainingautocounter.repository.dao.ExerciseDao
import com.myohoon.hometrainingautocounter.repository.dao.GoalsSettingDao
import com.myohoon.hometrainingautocounter.repository.dao.LogsDao
import com.myohoon.hometrainingautocounter.repository.entity.*
import com.myohoon.hometrainingautocounter.repository.enums.ExerciseType
import com.myohoon.hometrainingautocounter.repository.enums.GoalsSettingType

@Database(entities = [
    ExerciseEntity::class,
    Goal::class,
    ExerciseLogStart::class,
    ExerciseLogDetail::class,
    ExerciseLogEnd::class,
 ], version = 1)
abstract class AppDB : RoomDatabase() {
    abstract fun exerciseSettingDao(): ExerciseDao
    abstract fun goalsSettingDao(): GoalsSettingDao
    abstract fun logsDao(): LogsDao

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
                            db.execSQL("insert into exercise values ($i, '${v.title}', '${ExerciseEntity.IS_SHOW_EXPLANATION_DEFAULT_VALUE}');")

                            //goals 테이블 초기값
                            goals.forEachIndexed { j, v1 ->
                                db.execSQL("insert into goals_setting values ('${i}_${j}', $i, 0, '${GoalsSettingType.DEFAULT_VALUE}');")
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