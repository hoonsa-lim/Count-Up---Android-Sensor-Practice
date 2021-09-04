package com.myohoon.hometrainingautocounter.viewmodel

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.myohoon.hometrainingautocounter.repository.enums.Exercise

class ExerciseViewModel: ViewModel() {

    //운동 목록
    val exerciseList = ObservableField(enumValues<Exercise>().toList())

    val goCountFragment = ObservableBoolean(false)

    fun btnStartExerciseClicked(view: View){
        goCountFragment.set(true)
    }


    override fun onCleared() {
        super.onCleared()
    }
}