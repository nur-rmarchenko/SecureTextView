package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.secure_view_component.setupAsSecure
import com.google.android.material.bottomsheet.BottomSheetDialog

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        findViewById<TextView>(R.id.tvText).run {
            setupAsSecure()

            setOnClickListener {
                BottomSheetDialog(context).apply { setContentView(R.layout.bottom_sheet) }.show()
            }
        }
    }
}


