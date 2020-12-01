package com.example.css390_arapp

import android.os.Bundle
import android.provider.AlarmClock
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ar_render : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_render)

        // Get the Intent that started this activity and extract the string
        val message = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE)

        // Capture the layout's TextView and set the string as its text
        val textView = findViewById<TextView>(R.id.captured_coord).apply {
            text = message
        }

    }
}