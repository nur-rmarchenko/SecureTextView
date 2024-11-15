package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.secure_view_component.contracts.OnSecureGestureDelegate
import com.example.myapplication.secure_view_component.contracts.OnSecureGestureListener

class MainActivity : AppCompatActivity(), OnSecureGestureListener by OnSecureGestureDelegate() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        onActivityCreated(this)
    }
}


