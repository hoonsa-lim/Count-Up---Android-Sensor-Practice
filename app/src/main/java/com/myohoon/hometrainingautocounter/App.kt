package com.myohoon.hometrainingautocounter

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class App : Application() {
    companion object {
        const val TAG = "App"
    }

    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()

        //analytics
        firebaseAnalytics = Firebase.analytics

        //init admob
        MobileAds.initialize(this) { Log.d(TAG, "onCreate: ad init complete")}
    }
}