package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.secure_view_component.setupAsSecure
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.tvText).run {
            setupAsSecure()

            //BottomSheet
            setOnClickListener {
                BottomSheetDialog(context).apply {
                    setContentView(R.layout.bottom_sheet)
                    findViewById<TextView>(R.id.textView)?.setupAsSecure()
                }.show()
            }

            //Новый экран
            setOnLongClickListener {
                startActivity(Intent(context, SecondActivity::class.java))
                true
            }
        }
    }
}

