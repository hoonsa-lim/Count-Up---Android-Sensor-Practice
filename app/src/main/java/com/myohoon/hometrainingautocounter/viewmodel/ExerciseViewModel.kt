package com.myohoon.hometrainingautocounter.viewmodel

import android.app.Application
import android.content.Context
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.util.Log
import android.view.WindowManager
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.myohoon.hometrainingautocounter.R
import com.myohoon.hometrainingautocounter.repository.AppDB
import com.myohoon.hometrainingautocounter.repository.AppShared
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseEntity
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseLogDetail
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseLogStart
import com.myohoon.hometrainingautocounter.repository.entity.Goal
import com.myohoon.hometrainingautocounter.repository.enums.ExerciseType
import com.myohoon.hometrainingautocounter.repository.enums.GoalsSettingType
import com.myohoon.hometrainingautocounter.repository.enums.LogStatus
import com.myohoon.hometrainingautocounter.utils.SensorUtils
import com.myohoon.hometrainingautocounter.utils.SoundEffectUtils
import com.myohoon.hometrainingautocounter.utils.TimeUtils
import com.myohoon.hometrainingautocounter.view.fragment.main.CountFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

class ExerciseViewModel(app: Application): AndroidViewModel(app) {
    companion object{
        const val TAG = "ExerciseViewModel"
        private const val COUNT_CONDITION_PUSH_UP = 2.0 //근접 선서는 0.0 또는 5.0만 나타남.
        private const val COUNT_CONDITION_SQUAT = 2.0f //
    }

    //db
    private lateinit var db: AppDB
    private val prefs = AppShared.getInstance(app)

    //rx
    private val disposeBag = CompositeDisposable()

    //data
    private val totalGoals = ObservableField(mutableListOf<Goal>())                               // 목표의 배열
    val currentExercise = ObservableField<ExerciseEntity>()
    val currentGoals = ObservableField(mutableListOf<Goal>())
    val exerciseList = ObservableField(mutableListOf<ExerciseEntity>())                             //운동 목록
    val isSetGoals = ObservableBoolean(false)                                               //목표 설정 여부 목표 없이 운동하냐/ 설정 후 운동 하냐
    val isRingOn = ObservableBoolean(prefs.isRing)

    //count fragment data
    val currentSets = ObservableField(GoalsSettingType.DEFAULT_VALUE)
    val currentReps = ObservableField(GoalsSettingType.DEFAULT_VALUE)
    val currentTimeLimit = ObservableField(TimeUtils.secToFormatTime(GoalsSettingType.DEFAULT_VALUE.toInt()))
    val logs = ObservableField<MutableList<ExerciseLogDetail>>()
    val goCompleteFragment = ObservableBoolean(false)
    val showRestTimerAlert = ObservableBoolean(false)
    private var currentLogStart: ExerciseLogStart? = null                                               //count fragment 화면에서 현재 시작한 운동의 logStart
    private val beforeData = arrayOfNulls<Float>(3)

    //timer
    var timerDisposable: Disposable? = null

    //snackbar
    val snackBarMsg = ObservableField<Int>()

    //utils
    private var sensorUtils: SensorUtils? = null
    private val soundUtils = SoundEffectUtils.getInstance(app)


    //input event
    val createCountFragment = PublishSubject.create<ExerciseEntity>()
    val btnRestButtonClick = PublishSubject.create<Unit>()
    val exerciseQuit = PublishSubject.create<Unit>()

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
                if (isCompleteGoal(GoalsSettingType.REPS.ordinal, it))
                    startRest(GoalsSettingType.REPS.name)
            }
        }
    }
    private val setsCB = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            currentSets.get()?.let {
                if (isCompleteGoal(GoalsSettingType.SETS.ordinal, it)) exerciseEnd()
            }
        }
    }
    private val timeLimitCB = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            currentTimeLimit.get()?.let {
                if (isCompleteGoal(GoalsSettingType.TIME_LIMIT_PER_SET.ordinal, TimeUtils.formatTimeToSec(it).toString()))
                    startRest(GoalsSettingType.TIME_LIMIT_PER_SET.name)
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

        //rx style
        createCountFragment.subscribe{ exercise -> insertLogStart(exercise) }?.let { d -> disposeBag.add(d) }
        btnRestButtonClick.subscribe { startRest(GoalsSettingType.TIME_REST.name) }?.let { d->disposeBag.add(d) }
        exerciseQuit.subscribe{ exerciseEnd() }?.let { d->disposeBag.add(d) }
    }

    //페이지 이동 flag
    val goCountFragment = ObservableBoolean(false)                                            //count fragment 이동 여부

    //main activity 생성 시 호출
    fun initRoomDB(uid: String) {
        db = AppDB.instance(getApplication(), uid)

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

    private fun insertLogStart(exercise: ExerciseEntity) {
        val now = TimeUtils.getUnixTime()
        val formatTime = TimeUtils.unixTimeToFormatTime(now, getApplication()).split(" ")
        if (formatTime.isNullOrEmpty() || formatTime.size < 2) return
        val logStart = ExerciseLogStart(
            "${exercise.eId}_${now}",
            exercise.eId, formatTime.first(), formatTime.last(),
        )

        db.logsDao().insertLogStart(logStart)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                currentLogStart = logStart
                getNowExerciseLogs()
                Log.d(TAG, "insertLogStart: success")
            },{
                currentLogStart = null
                Log.d(TAG, "insertLogStart: fail")
            })?.let { d ->  disposeBag.add(d)}
    }

    private fun getNowExerciseLogs(){
        currentLogStart?.let {
            db.logsDao().selectLogDetailsByLogStartId(it.logStartId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ newList ->
                    logs.set(newList)
                    Log.d(TAG, "getNowExerciseLogs: success size == ${newList.size}")
                },{
                    Log.d(TAG, "getExerciseList: error == ${it.message}")
                })?.let { d ->  disposeBag.add(d)}
        }
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
                .filter { isExercising() }
                .filter { getCountCondition(exercise.eId, it) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { processingSensorData(it) }?.let { d -> disposeBag.add(d) }
        }
    }

    private fun isExercising():Boolean {

        return true
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

    private fun getCountCondition(exerciseId: Int, sensorEvent: SensorEvent): Boolean{
        when(exerciseId){
            ExerciseType.PUSH_UP.ordinal -> return sensorEvent.values[0] < COUNT_CONDITION_PUSH_UP
            ExerciseType.SQUAT.ordinal -> {
                //초기화
//                if (beforeData[0] == null) beforeData[0] = sensorEvent.values[2]
//
////                Log.d(TAG, "getCountCondition:  == ${sensorEvent.values[2] - (beforeData[0]?:0f)}")
//                Log.d(TAG, "getCountConditionx: beforeData == ${(beforeData[0]?:0f)}, z == ${sensorEvent.values[0]}")
//                Log.d(TAG, "getCountConditiony: beforeData == ${(beforeData[0]?:0f)}, z == ${sensorEvent.values[1]}")
//                Log.d(TAG, "getCountConditionz: beforeData == ${(beforeData[0]?:0f)}, z == ${sensorEvent.values[2]}")
//                Log.d(TAG, "getCountCondition:  == ===============================")
//
////                //비교
////                if ((beforeData[0]!! - sensorEvent.values[2].absoluteValue) > COUNT_CONDITION_SQUAT){
////                    beforeData[0] = sensorEvent.values[2].absoluteValue
////                    return beforeData[0]!! > sensorEvent.values[2].absoluteValue
////                }else {
////                    return false
////                }
                return true
            }
            ExerciseType.CHIN_UP.ordinal -> {
                return true
            }
            ExerciseType.SIT_UP.ordinal -> {
                return true
            }
            ExerciseType.PLANK.ordinal -> {
                return true
            }
            else -> return true
        }
    }

    private fun startRest(cause: String) {
        Log.d(TAG, "startRest: $cause")
        //조건: 수동으로 휴식 클릭 시(cause == TIME_REST)
        //또는 세트 목표 활성화 && 마지막 세트가 아닐 경우(마지막 세트의 경우 휴식 다이얼로그 x)
        if (cause == GoalsSettingType.TIME_REST.name
            || isActiveCurrentGoal(GoalsSettingType.SETS.ordinal) && (currentSets.get() ?: GoalsSettingType.DEFAULT_VALUE).toInt()+1 < getCurrentGoalValue(GoalsSettingType.SETS.ordinal).toInt()){
            showRestTimerAlert.set(true)
        }

        insertLogDetail(cause)
        startTimer(false)
        resetData(false)
        setsCountUp()
    }

    private fun insertLogDetail(cause: String) {
        currentLogStart?.let {
            val reps = if(it.eId == ExerciseType.PLANK.ordinal) null else currentReps.get()!!.toInt()
            val timeSpent = TimeUtils.formatTimeToSec(currentTimeLimit.get()!!)
            val status = getLogDetail(it, cause)

            val logDetail = ExerciseLogDetail(
                logStartId = it.logStartId,
                count = reps,
                timeSpent = timeSpent,
                status = status,
                saveAt = TimeUtils.currentFormatTime(getApplication())
            )

            db.logsDao().insertLogDetail(logDetail)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d(TAG, "insertLogDetail: success")
                },{
                    Log.d(TAG, "insertLogDetail: fail")
                })?.let { d ->  disposeBag.add(d)}
        }
    }

    private fun getLogDetail(logStart: ExerciseLogStart, cause: String): String {
        val context = getApplication<Application>().applicationContext

        //플랭크의 경우
        if (logStart.eId == ExerciseType.PLANK.ordinal){
            if (cause == GoalsSettingType.TIME_LIMIT_PER_SET.name
                && isActiveCurrentGoal(GoalsSettingType.TIME_LIMIT_PER_SET.ordinal))
                    return context.getString(LogStatus.SUCCESS.strRes)
            else if(cause == GoalsSettingType.TIME_REST.name
                && isActiveCurrentGoal(GoalsSettingType.TIME_LIMIT_PER_SET.ordinal))
                    return context.getString(LogStatus.EARLY_FINISH.strRes)
            else
                return context.getString(LogStatus.MANUALLY_FINISH.strRes)
        }else{

            //플랭크 외 운동일 경우
            if (cause == GoalsSettingType.REPS.name) return context.getString(LogStatus.SUCCESS.strRes)
            else if(cause == GoalsSettingType.TIME_LIMIT_PER_SET.name) return context.getString(LogStatus.TIME_OVER.strRes)
            else if (cause == GoalsSettingType.TIME_REST.name
                && (isActiveCurrentGoal(GoalsSettingType.TIME_LIMIT_PER_SET.ordinal)
                        || isActiveCurrentGoal(GoalsSettingType.REPS.ordinal)))
                            return context.getString(LogStatus.EARLY_FINISH.strRes)
            else
                return context.getString(LogStatus.MANUALLY_FINISH.strRes)
        }
    }

    private fun setsCountUp() {
        currentSets.get()?.let { currentSets.set("${it.toInt() + 1}") }
    }

    private fun repsCountUp() {
        currentReps.get()?.let {
            if (it.toInt() == 0) startTimer(true)
            currentReps.set("${it.toInt() + 1}")

            //알림음
            if (isRingOn.get()) soundUtils.noti.onNext(SoundEffectUtils.TYPE_COUNT)
        }
    }

    private fun startTimer(isStart:Boolean) {
        if (isStart){
            io.reactivex.Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    currentTimeLimit.set(TimeUtils.secToFormatTime(it.toInt() + 1))
                }?.let { d->
                    timerDisposable = d
                    disposeBag.add(d)
                }
        }else{
            timerDisposable?.let { it.dispose() }
        }
    }

    private fun resetData(isAllClear:Boolean) {
        currentReps.set(GoalsSettingType.DEFAULT_VALUE)
        currentTimeLimit.set(TimeUtils.secToFormatTime(GoalsSettingType.DEFAULT_VALUE.toInt()))
        if (isAllClear) {
            currentSets.set(GoalsSettingType.DEFAULT_VALUE)
            logs.set(null)
        }
    }

    private fun exerciseEnd(){
        resetData(true)
        startTimer(false)
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
        soundUtils.clearResource()
    }

    fun getCurrentGoalValue(goalType: Int):String {
        val e = currentExercise.get()
        val g = currentGoals.get()
        if (e == null || g == null) return GoalsSettingType.DEFAULT_VALUE

        val goal = g.filter {it.eId == e.eId && it.goalId.split("_").last().toInt() == goalType }
        if (goal.isEmpty()) return GoalsSettingType.DEFAULT_VALUE

        return goal.first().lastGoalsValue
    }

    fun isActiveCurrentGoal(typeOrdinal: Int): Boolean{
        val e = currentExercise.get() ?: return false
        val g = currentGoals.get() ?: return false

        val goal = g.filter {it.eId == e.eId && it.goalId.split("_").last().toInt() == typeOrdinal }
        if (goal.isEmpty()) return false

        return goal.first().isActive
    }

    fun btnRingClick(){
        isRingOn.set(isRingOn.get().not())
    }
}