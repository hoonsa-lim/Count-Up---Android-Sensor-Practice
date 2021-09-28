 package com.myohoon.hometrainingautocounter.utils

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class SoundEffectUtils(val app: Application) {
    companion object{
        const val TAG = "SoundEffectUtils"

        //진동 시간
        private const val VIBRATE_DURATION = 300L        //밀리초

        //알림음 타입
        const val TYPE_COUNT = 0
        const val TYPE_TIME = 1

        //싱글톤
        private var sUtil: SoundEffectUtils? = null
        fun getInstance(app: Application): SoundEffectUtils{
            if (sUtil == null) sUtil = SoundEffectUtils(app)
            return sUtil!!
        }
    }

    //rx
    private val disposable = CompositeDisposable()

    //알림 객체
    private val mAudio = app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var ringTone: Ringtone? = null
    private var vibrator: Vibrator? = null
    private var vibrationEffect: VibrationEffect? = null

    //input event 알림 신호
    val noti = PublishSubject.create<Int>()

    //초기화
    init {
        noti.subscribe { ringSound(it) }?.let { d -> disposable.add(d)  }
    }

    private fun ringSound(typeCode:Int) {

        when(mAudio.ringerMode){
            //무음
            AudioManager.RINGER_MODE_SILENT -> {

            }

            //진동
            AudioManager.RINGER_MODE_VIBRATE -> {
                if (vibrator == null)
                    vibrator = app.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                vibrator?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (vibrationEffect == null)
                            vibrationEffect = VibrationEffect.createOneShot(VIBRATE_DURATION, VibrationEffect.DEFAULT_AMPLITUDE)
                        it.vibrate(vibrationEffect)
                    }else{
                        it.vibrate(VIBRATE_DURATION)
                    }
                }
            }

            //소리
            AudioManager.RINGER_MODE_NORMAL -> {
                if (ringTone == null){
                    val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    ringTone = RingtoneManager.getRingtone(app, uri)
                }

                ringTone?.let {
                    it.stop()       //이전에 발생한 효과음이 끝나기 전에 실행 됐을 때는 종료하고 시작해줘야함.
                    it.play()
                }
            }
        }
    }

    fun clearResource(){
        if (!disposable.isDisposed) disposable.dispose()
    }
}