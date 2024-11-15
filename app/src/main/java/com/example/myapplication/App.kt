package com.example.myapplication

import android.app.Application
import com.example.myapplication.secure_view_component.contracts.OnApplicationSecureGestureListener
import com.example.myapplication.secure_view_component.contracts.ApplicationSecureGestureDelegate

class App : Application(), OnApplicationSecureGestureListener by ApplicationSecureGestureDelegate() {

    override fun onCreate() {
        super.onCreate()
        onApplicationCreated(getSharedPreferences("app", MODE_PRIVATE))
    }

}


