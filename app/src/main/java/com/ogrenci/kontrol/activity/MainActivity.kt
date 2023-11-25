package com.ogrenci.kontrol.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ogrenci.kontrol.databinding.ActivityMainBinding
import com.onesignal.OneSignal

class MainActivity : AppCompatActivity() {

    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

    }

}