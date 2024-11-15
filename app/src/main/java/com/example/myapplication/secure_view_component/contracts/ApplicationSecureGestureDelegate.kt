package com.example.myapplication.secure_view_component.contracts

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.edit
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class ApplicationSecureGestureDelegate : OnApplicationSecureGestureListener, SensorEventListener {

    private lateinit var _prefs: SharedPreferences
    private lateinit var _context: Context

    private lateinit var _sensorManager: SensorManager
    private lateinit var _vibrator: Vibrator
    private var _accelerometer: Sensor? = null

    private var _isScreenDown = false
    private var _screenDownTriggerTime: Long = 0
    private var _isSecuredNow = false
    private var _isAppActiveNow = false

    override val isSecureNow: Boolean get() = _isSecuredNow

    override fun onApplicationCreated(application: Application) {
        _context = application.applicationContext
        _prefs = _context.getSharedPreferences("AAAA", Context.MODE_PRIVATE)
        _isSecuredNow = _prefs.getBoolean(BROADCAST_TAG, false)
        initComponents()
        setupAppLifecycleListener()
        _sensorManager.registerListener(this, _accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun setupAppLifecycleListener() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                _isAppActiveNow = true
                _sensorManager
                    .registerListener(
                        this@ApplicationSecureGestureDelegate,
                        _accelerometer,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
            }

            override fun onStop(owner: LifecycleOwner) {
                _isAppActiveNow = false
                _sensorManager.unregisterListener(this@ApplicationSecureGestureDelegate)
                super.onStop(owner)
            }
        })
    }

    private fun initComponents() {
        _sensorManager = _context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        _accelerometer = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        _vibrator = _context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun switchSecuredState() {
        _isSecuredNow = !_isSecuredNow
        _prefs.edit { putBoolean(BROADCAST_TAG, _isSecuredNow) }
        LocalBroadcastManager.getInstance(_context).sendBroadcast(Intent(BROADCAST_TAG))
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER && _isAppActiveNow) {
            val zAxis = event.values[2]

            if (zAxis < -8.0 && !_isScreenDown) {
                _isScreenDown = true
                _screenDownTriggerTime = System.currentTimeMillis()
            } else if (zAxis > 8.0 && _isScreenDown) {
                val upTime = System.currentTimeMillis()
                val timeDiff = upTime - _screenDownTriggerTime
                if (timeDiff <= GESTURE_THRESHOLD) {
                    switchSecuredState()
                    pushVibration()
                }
                _isScreenDown = false
            }
        }
    }

    private fun pushVibration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
            _vibrator.vibrate(vibrationEffect)
        } else {
            _vibrator.vibrate(100)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    companion object {
        private const val GESTURE_THRESHOLD = 1000L
    }
}