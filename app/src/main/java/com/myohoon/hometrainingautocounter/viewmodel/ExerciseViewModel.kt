package com.myohoon.hometrainingautocounter.viewmodel

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.util.Log
import android.view.View
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.myohoon.hometrainingautocounter.R
import com.myohoon.hometrainingautocounter.repository.AppDB
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseEntity
import com.myohoon.hometrainingautocounter.repository.entity.Goal
import com.myohoon.hometrainingautocounter.repository.enums.ExerciseType
import com.myohoon.hometrainingautocounter.repository.enums.GoalsSettingType
import com.myohoon.hometrainingautocounter.repository.model.ExerciseLog
import com.myohoon.hometrainingautocounter.utils.AlertUtils
import com.myohoon.hometrainingautocounter.utils.SensorUtils
import com.myohoon.hometrainingautocounter.utils.TimeUtils
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
    val currentSets = ObservableField(GoalsSettingType.DEFAULT_VALUE)
    val currentReps = ObservableField(GoalsSettingType.DEFAULT_VALUE)
    val currentTimeLimit = ObservableField(TimeUtils.secToFormatTime(GoalsSettingType.DEFAULT_VALUE.toInt()))
    val logs = ObservableField<MutableList<ExerciseLog>>()
    val goCompleteFragment = ObservableBoolean(false)
    val showRestTimerAlert = ObservableBoolean(false)

    //snackbar
    val snackBarMsg = ObservableField<Int>()

    //utils
    private var sensorUtils: SensorUtils? = null

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
    private val repsCB = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            currentReps.get()?.let {
                if (isCompleteGoal(GoalsSettingType.REPS.ordinal, it)) startRest()
            }
        }
    }
    private val setsCB = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            currentSets.get()?.let {
                if (isCompleteGoal(GoalsSettingType.SETS.ordinal, it)) exerciseComplete()
            }
        }
    }
    private val timeLimitCB = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            currentTimeLimit.get()?.let {
                if (isCompleteGoal(GoalsSettingType.TIME_LIMIT_PER_SET.ordinal, it)) startRest()
            }
        }
    }

    private fun isCompleteGoal(goalType: Int, currentCount: String):Boolean {
        val exercise = currentExercise.get() ?: return false
        val goals = currentGoals.get() ?: return false
        val goal = goals.filter {
            it.eId == exercise.eId && it.goalId.split("_").last().toInt()==goalType
        }
        if (goal.isEmpty() || goal.first().isActive.not() || goal.first().lastGoalsValue != currentCount)
            return false

        return true
    }

    //init
    init {
        totalGoals.addOnPropertyChangedCallback(totalCB)
        currentExercise.addOnPropertyChangedCallback(currentExerciseCB)
        currentReps.addOnPropertyChangedCallback(repsCB)
        currentSets.addOnPropertyChangedCallback(setsCB)
        currentTimeLimit.addOnPropertyChangedCallback(timeLimitCB)
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
        if (isSetGoals){
            currentGoals.get()?.let {
                val list = it.filter { it.isActive && (it.lastGoalsValue == GoalsSettingType.DEFAULT_VALUE) }
                if (list.isEmpty().not()){
                    snackBarMsg.set(R.string.required_setting_goals)
                    return
                }
            }
        }

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

                //플랭크의 경우 개수 사용 안하기 때문에 삭제함.
                if (e.eId == ExerciseType.PLANK.ordinal){
                    l.removeAt(GoalsSettingType.REPS.ordinal)
                }
                currentGoals.set(l)
            }
        }
    }

    fun initSensor(sensorManager: SensorManager){
        currentExercise.get()?.let { exercise ->
            sensorUtils?.disposeSensor()
            sensorUtils = SensorUtils(sensorManager, exercise.eId)
            sensorUtils!!.sensorEvent
                .filter { sensorUtils!!.getFilter(exercise.eId, it) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { processingSensorData(it) }?.let { d -> disposeBag.add(d) }
        }
    }

    private fun processingSensorData(sensorEvent: SensorEvent) {
        currentExercise.get()?.let {
            when(it.eId){
                ExerciseType.PUSH_UP.ordinal,
                ExerciseType.SQUAT.ordinal,
                ExerciseType.CHIN_UP.ordinal,
                ExerciseType.SIT_UP.ordinal-> repsCountUp()
                ExerciseType.PLANK.ordinal -> {

                }
                else -> {
                    Log.d(TAG, "processingSensorData: error")
                }
            }
        }
    }

    private fun startRest() {
        resetData(false)
        setsCountUp()
        showRestTimerAlert.set(true)
    }

    private fun setsCountUp() {
        //TODO 알림음

        currentSets.get()?.let { currentSets.set("${it.toInt() + 1}") }
    }

    private fun repsCountUp() {
        currentReps.get()?.let { currentReps.set("${it.toInt() + 1}") }
    }

    private fun resetData(isAllClear:Boolean) {
        currentReps.set(GoalsSettingType.DEFAULT_VALUE)
        currentTimeLimit.set(TimeUtils.secToFormatTime(GoalsSettingType.DEFAULT_VALUE.toInt()))
        if (isAllClear) currentSets.set(GoalsSettingType.DEFAULT_VALUE)
    }

    private fun exerciseComplete() {
        resetData(true)
        goCompleteFragment.set(true)
    }

    override fun onCleared() {
        super.onCleared()

        //remove callback
        totalGoals.removeOnPropertyChangedCallback(totalCB)
        currentExercise.removeOnPropertyChangedCallback(currentExerciseCB)
        currentReps.removeOnPropertyChangedCallback(repsCB)
        currentSets.removeOnPropertyChangedCallback(setsCB)
        currentTimeLimit.removeOnPropertyChangedCallback(timeLimitCB)

        //rx
        if (!disposeBag.isDisposed) disposeBag.dispose()

        //sensor
        sensorUtils?.let { it.disposeSensor() }
    }

    fun getCurrentGoalValue(goalType: Int):String {
        val e = currentExercise.get()
        val g = currentGoals.get()
        if (e == null || g == null) return GoalsSettingType.DEFAULT_VALUE

        val goal = g.filter {it.eId == e.eId && it.goalId.split("_").last().toInt() == goalType }
        if (goal.isEmpty()) return GoalsSettingType.DEFAULT_VALUE

        return goal.first().lastGoalsValue
    }
}