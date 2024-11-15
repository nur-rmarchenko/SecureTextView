package com.example.myapplication.secure_view_component.contracts

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.myapplication.secure_view_component.SecureTextView

class OnSecureGestureDelegate : OnSecureGestureListener, SensorEventListener {

    private lateinit var _activity: FragmentActivity
    private lateinit var _sensorManager: SensorManager
    private lateinit var _vibrator: Vibrator
    private var _accelerometer: Sensor? = null
    private var _isScreenDown = false
    private var _downTime: Long = 0
    private var _secureTextViews = mutableListOf<SecureTextView>()
    private var _isCheckedEarlier = false


    override fun onActivityCreated(activity: FragmentActivity) {
        _activity = activity
        _sensorManager = _activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        _accelerometer = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        _vibrator = _activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        _activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                _sensorManager
                    .registerListener(
                        this@OnSecureGestureDelegate,
                        _accelerometer,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                _sensorManager.unregisterListener(this@OnSecureGestureDelegate)
            }
        })
        updateViewsState(false)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val zAxis = event.values[2]

            if (zAxis < -8.0 && !_isScreenDown) {
                _isScreenDown = true
                _downTime = System.currentTimeMillis()
            } else if (zAxis > 8.0 && _isScreenDown) {
                val upTime = System.currentTimeMillis()
                val timeDiff = upTime - _downTime
                if (timeDiff <= GESTURE_THRESHOLD) {
                    switchSecuredState()
                    updateViewsState(true)
                }
                _isScreenDown = false
            }
        }
    }

    private fun switchSecuredState() {
        (_activity.applicationContext as OnApplicationSecureGestureListener).switchSecuredState()
    }

    private fun updateViewsState(useVibrationFeedback: Boolean) {
        if (!_isCheckedEarlier) {
            val androidContent = _activity.findViewById<View>(android.R.id.content)
            findAndUpdateAllSecureFields(androidContent)
            _isCheckedEarlier = true
        }

        val isSecuredNow = (_activity.applicationContext as OnApplicationSecureGestureListener).isSecuredNow()
        _secureTextViews.forEach { it.updateSecureState(isSecuredNow) }

        if (useVibrationFeedback) pushVibration()
    }


    private fun findAndUpdateAllSecureFields(view: View?) {
        when (view) {
            is ViewGroup -> {
                for (i in 0 until view.childCount) {
                    val child = view.getChildAt(i)
                    findAndUpdateAllSecureFields(child)
                }
            }
            is SecureTextView -> _secureTextViews.add(view)
        }
    }

    private fun pushVibration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 50, 50, 100, 50, 200)
            val vibrationEffect = VibrationEffect.createWaveform(pattern, -1)
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