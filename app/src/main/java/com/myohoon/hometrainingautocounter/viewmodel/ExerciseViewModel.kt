package com.myohoon.hometrainingautocounter.viewmodel

import android.content.Context
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.myohoon.hometrainingautocounter.repository.AppDB
import com.myohoon.hometrainingautocounter.repository.enums.ExerciseType
import com.myohoon.hometrainingautocounter.repository.enums.GoalsSettingType

class ExerciseViewModel(context: Context, uid:String): ViewModel() {

    //운동 목록
    val exerciseList = ObservableField(enumValues<ExerciseType>().toList())
    val goalsTypes = ObservableField(enumValues<GoalsSettingType>().toList())
    val goCountFragment = ObservableBoolean(false)
    private var db = AppDB.instance(context, uid)


    fun btnStartExerciseClicked(view: View){
        goCountFragment.set(true)
    }


    override fun onCleared() {
        super.onCleared()
    }
}