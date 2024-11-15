package com.example.myapplication.secure_view_component.contracts

import android.app.Application

interface OnApplicationSecureGestureListener {
    val BROADCAST_TAG: String get() = "UPDATE_SECURE_STATE"
    val isSecureNow: Boolean
    fun onApplicationCreated(application: Application)
    fun switchSecuredState()
}