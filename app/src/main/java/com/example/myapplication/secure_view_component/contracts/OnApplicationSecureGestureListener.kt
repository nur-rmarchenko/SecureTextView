package com.example.myapplication.secure_view_component.contracts

import android.content.SharedPreferences

interface OnApplicationSecureGestureListener {
    fun onApplicationCreated(appPreferences: SharedPreferences)
    fun isSecuredNow(): Boolean
    fun switchSecuredState()
}