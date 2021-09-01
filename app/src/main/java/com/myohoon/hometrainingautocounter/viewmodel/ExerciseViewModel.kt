package com.myohoon.hometrainingautocounter.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.myohoon.hometrainingautocounter.repository.enums.Exercise

class ExerciseViewModel(): ViewModel() {

    //운동 목록
    val exerciseList = ObservableField(enumValues<Exercise>().toList())

    override fun onCleared() {
        super.onCleared()
    }
}