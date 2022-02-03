package com.example.dietapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.Timer
import kotlin.concurrent.timer

class Timer : AppCompatActivity() {

    private var time = 0
    private var timerTask : Timer? = null

    lateinit var secTextView: TextView
    lateinit var milliTextView: TextView
    lateinit var settingEditText: EditText
    lateinit var settingButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

            secTextView = findViewById<TextView>(R.id.secTextView)
            milliTextView = findViewById<TextView>(R.id.milliTextView)
            settingEditText = findViewById<EditText>(R.id.settingEditText)
            settingButton = findViewById<Button>(R.id.settingButton)

            settingButton.setOnClickListener {
                time = settingEditText.text.toString().toInt()*100
                start()
            }
        }


         private fun start() {

            timerTask = timer(period=10){
                time--
                val sec = time / 100
                val milli = time % 100

                if (time == 0) {
                    timerTask?.cancel()
                }

                runOnUiThread {
                    if (time != 0) {
                        secTextView.text = "$sec"
                        milliTextView.text = "$milli"
                    } else {
                        secTextView.text = "0"
                        milliTextView.text = "00"
                    }

                }
            }
         }
}