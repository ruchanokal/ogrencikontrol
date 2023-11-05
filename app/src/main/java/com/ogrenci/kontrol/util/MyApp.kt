package com.ogrenci.kontrol.util

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.ogrenci.kontrol.model.Ogrenci
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val ONESIGNAL_APP_ID = "93e2b12f-43f5-4d79-9974-6b1bba9411dc"

class MyApp :Application() {
    private var tumOgrenciList: ArrayList<Ogrenci>? = null

    fun getTumOgrenciList(): ArrayList<Ogrenci>? {
        return tumOgrenciList
    }

    fun setTumOgrenciList(list: ArrayList<Ogrenci>?) {
        if (list != null) {
            tumOgrenciList?.clear()
            tumOgrenciList?.addAll(list)
        }
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Verbose Logging set to help debug issues, remove before releasing your app.
        OneSignal.Debug.logLevel = LogLevel.VERBOSE

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)

        // requestPermission will show the native Android notification permission prompt.
        // NOTE: It's recommended to use a OneSignal In-App Message to prompt instead.
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(true)
        }
    }
}