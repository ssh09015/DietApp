package com.example.dietapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    lateinit var recobutton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recobutton=findViewById(R.id.recobutton)

        recobutton.setOnClickListener {
            var intent=Intent(this, reco_op1::class.java)
            startActivity(intent)
        }
    }
}