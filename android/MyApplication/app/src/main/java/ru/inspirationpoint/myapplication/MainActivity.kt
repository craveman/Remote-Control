package ru.inspirationpoint.myapplication

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var checkBox = findViewById<CheckBox>(R.id.check1)
        checkBox.setOnCheckedChangeListener { btn, a ->
            run {
                if (!a)
                    findViewById<TextView>(R.id.textView).text = "Goodbye"

            }
        }

    }

}
