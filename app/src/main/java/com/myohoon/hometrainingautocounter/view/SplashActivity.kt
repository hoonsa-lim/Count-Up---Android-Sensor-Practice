package com.myohoon.hometrainingautocounter.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.myohoon.hometrainingautocounter.R
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {
    companion object{
        const val TAG = "SplashActivity"
        private val SPLASH_DELAY = 2000L
    }

    //rx
    private val disposeBag = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        delayIntent(true)
    }

    private fun delayIntent(isMain:Boolean){
        Observable.timer(SPLASH_DELAY, TimeUnit.MILLISECONDS)
            .subscribe({
                startActivity(Intent(baseContext, if (isMain) MainActivity::class.java else LoginActivity::class.java))
                finish()
            },{})?.let { d -> disposeBag.add(d) }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (disposeBag.isDisposed.not()) disposeBag.dispose()
    }
}