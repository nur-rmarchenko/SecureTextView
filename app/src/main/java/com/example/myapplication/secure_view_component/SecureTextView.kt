package com.example.myapplication.secure_view_component

import android.content.Context
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import com.example.myapplication.secure_view_component.contracts.OnApplicationSecureGestureListener
import com.google.android.material.textview.MaterialTextView

class SecureTextView(context: Context, attrs: AttributeSet) : MaterialTextView(context, attrs) {

    private var _isSecured = false

    fun updateSecureState(isSecured: Boolean) {
        _isSecured = isSecured
        setupAsSecure()
    }

    private fun setupAsSecure() {
        transformationMethod = if (_isSecured) PasswordTransformationMethod.getInstance() else null
    }
}