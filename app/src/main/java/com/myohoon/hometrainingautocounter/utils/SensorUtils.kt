package com.myohoon.hometrainingautocounter.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.myohoon.hometrainingautocounter.repository.enums.ExerciseType
import io.reactivex.subjects.PublishSubject

class SensorUtils(
    private val sensorManager: SensorManager, exerciseId: Int
): SensorEventListener {
    companion object{
        const val TAG = "SensorUtils"
    }
    private var sensor: Sensor? = null
    val sensorEvent = PublishSubject.create<SensorEvent>()

    //운동 종류에 맞는 sensor 초기화
    init {
        when(exerciseId){
            ExerciseType.PUSH_UP.ordinal -> {
                sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
            }
            ExerciseType.SQUAT.ordinal -> {
                sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
            }
            ExerciseType.CHIN_UP.ordinal -> {

            }
            ExerciseType.SIT_UP.ordinal -> {

            }
            ExerciseType.PLANK.ordinal -> {

            }
        }

        //리스너 등록
        sensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            sensorEvent.onNext(it)
        }
        Log.d(TAG, "onSensorChanged: $sensorEvent")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    fun disposeSensor(){
        sensorManager.unregisterListener(this)
    }
}