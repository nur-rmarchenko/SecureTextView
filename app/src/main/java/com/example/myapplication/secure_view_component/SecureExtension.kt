package com.example.myapplication.secure_view_component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.method.PasswordTransformationMethod
import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.myapplication.secure_view_component.contracts.OnApplicationSecureGestureListener

fun TextView.setupAsSecure() {
    val secureGestureListener = context?.applicationContext as? OnApplicationSecureGestureListener
        ?: return

    fun updateViewState() {
        transformationMethod = if (secureGestureListener.isSecureNow) {
            PasswordTransformationMethod.getInstance()
        } else null
    }

    fun setupReceiver() {
        val manager = LocalBroadcastManager.getInstance(context.applicationContext)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                updateViewState()
            }
        }
        manager.registerReceiver(receiver, IntentFilter(secureGestureListener.BROADCAST_TAG))
        findViewTreeLifecycleOwner()?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                manager.unregisterReceiver(receiver)
                super.onDestroy(owner)
            }
        })
    }

    updateViewState()
    setupReceiver()
}