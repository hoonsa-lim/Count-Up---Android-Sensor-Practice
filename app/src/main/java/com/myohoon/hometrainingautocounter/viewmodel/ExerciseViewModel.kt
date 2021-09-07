package com.myohoon.hometrainingautocounter.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.myohoon.hometrainingautocounter.repository.AppDB
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseEntity
import com.myohoon.hometrainingautocounter.repository.entity.Goal
import com.myohoon.hometrainingautocounter.repository.model.ExerciseLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList

class ExerciseViewModel: ViewModel() {
    companion object{
        const val TAG = "ExerciseViewModel"
    }
    //db
    private lateinit var db: AppDB

    //rx
    private val disposeBag = CompositeDisposable()

    //data
    private val totalGoals = ObservableField(mutableListOf<Goal>())                               // 목표의 배열
    val currentExercise = ObservableField<ExerciseEntity>()
    val currentGoals = ObservableField(mutableListOf<Goal>())
    val exerciseList = ObservableField(mutableListOf<ExerciseEntity>())                             //운동 목록
    val isSetGoals = ObservableBoolean(false)                                               //목표 설정 여부 목표 없이 운동하냐/ 설정 후 운동 하냐

    //count fragment data
    val currentSets = ObservableField("0")
    val currentReps = ObservableField("0")
    val currentTimeLimit = ObservableField("00:00")
    val logs = ObservableField<MutableList<ExerciseLog>>()

    //callback
    private val totalCB = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            initCurrentGoals()
        }
    }

    private val currentExerciseCB = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            initCurrentGoals()
        }
    }

    //init
    init {
        totalGoals.addOnPropertyChangedCallback(totalCB)
        currentExercise.addOnPropertyChangedCallback(currentExerciseCB)
    }

    //페이지 이동 flag
    val goCountFragment = ObservableBoolean(false)                                            //count fragment 이동 여부

    //main activity 생성 시 호출
    fun initRoomDB(context: Context, uid: String) {
        db = AppDB.instance(context, uid)

        //운동 목록 및 설정 관련 데이터
        getExerciseList()

        //해당 운동의 목표 설정 했던 내용
        getGoals()
    }

    fun btnStartExerciseClicked(isSetGoals:Boolean){
        this.isSetGoals.set(isSetGoals)
        goCountFragment.set(true)
    }

    private fun getExerciseList(){
        db.exerciseSettingDao().selectAll()
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                exerciseList.set(it)
            },{
                Log.d(TAG, "getExerciseList: error == ${it.message}")
            })?.let { d ->  disposeBag.add(d)}
    }

    private fun getGoals(){
        db.goalsSettingDao().selectGoals()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ newList ->
                totalGoals.set(newList)
            },{
                Log.d(TAG, "getExerciseList: error == ${it.message}")
            })?.let { d ->  disposeBag.add(d)}
    }

    fun updateGoal(goal: Goal){
        db.goalsSettingDao().insert(goal)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, "updateIsShowExplanation: success == ${goal.goalId}")
            },{
                Log.d(TAG, "updateIsShowExplanation: error == ${goal.goalId}, ${it.message}")
            })?.let { d -> disposeBag.add(d) }
    }

    fun updateIsShowExplanation(data: ExerciseEntity){
        if (data.isShowExplanation == null) return

        db.exerciseSettingDao().update(data.isShowExplanation!!, data.eId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, "updateIsShowExplanation: success == ${data.eName}")
            },{
                Log.d(TAG, "updateIsShowExplanation: error == ${data.eName}, ${it.message}")
            })?.let { d -> disposeBag.add(d) }
    }

    //전체 목표 목록중 현재 선택한 운동의 목록으로 filter 하며 저장
    private fun initCurrentGoals() {
        totalGoals.get()?.let { list ->
            currentExercise.get()?.let { e ->
                val l = list.filter { it.eId == e.eId }.toMutableList()
                l.sortBy { it.goalId }
                currentGoals.set(l)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        totalGoals.removeOnPropertyChangedCallback(totalCB)
        currentExercise.addOnPropertyChangedCallback(currentExerciseCB)
        if (!disposeBag.isDisposed) disposeBag.dispose()
    }
}