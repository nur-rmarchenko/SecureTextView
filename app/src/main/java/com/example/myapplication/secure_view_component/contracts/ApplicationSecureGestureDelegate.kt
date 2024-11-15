package com.example.myapplication.secure_view_component.contracts

import android.content.SharedPreferences
import androidx.core.content.edit

class ApplicationSecureGestureDelegate : OnApplicationSecureGestureListener {

    private lateinit var _prefs: SharedPreferences
    private var isSecured = false

    override fun onApplicationCreated(appPreferences: SharedPreferences) {
        _prefs = appPreferences
        isSecured = _prefs.getBoolean(TV_SECURE_TAG, false)
    }

    override fun isSecuredNow(): Boolean = isSecured

    override fun switchSecuredState() {
        isSecured = !isSecured
        _prefs.edit { putBoolean(TV_SECURE_TAG, isSecured) }
    }

    companion object {
        private const val TV_SECURE_TAG = "isTextViewSecuredNow"
    }
}