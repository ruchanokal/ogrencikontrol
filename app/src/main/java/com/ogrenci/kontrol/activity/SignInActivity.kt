package com.ogrenci.kontrol.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ogrenci.kontrol.R
import com.onesignal.OneSignal

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        OneSignal.initWithContext(this)
    }
}