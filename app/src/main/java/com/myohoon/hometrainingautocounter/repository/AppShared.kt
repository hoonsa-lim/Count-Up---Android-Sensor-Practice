package com.myohoon.hometrainingautocounter.repository

import android.content.Context
import android.content.SharedPreferences

class AppShared {
    companion object{
        const val TAG = "AppShared"
        private var appShared: AppShared? = null
        private var prefs : SharedPreferences? = null
        private const val PREFS_AUTO_END_REST = "autoRestEnd"
        private const val PREFS_RING_MODE = "ringMode"

        fun getInstance(context: Context):AppShared {
            if (prefs == null) prefs = context.getSharedPreferences(TAG, 0)
            if (appShared == null) appShared = AppShared()
            return appShared!!
        }
    }

    var isAutoEndRestTime: Boolean
        get() = prefs!!.getBoolean(PREFS_AUTO_END_REST, false)!!
        set(value) = prefs!!.edit().putBoolean(PREFS_AUTO_END_REST, value).apply()

    var isRing: Boolean
        get() = prefs!!.getBoolean(PREFS_RING_MODE, false)!!
        set(value) = prefs!!.edit().putBoolean(PREFS_RING_MODE, value).apply()
}