package com.example.dietapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class reco_op1 : AppCompatActivity() {
    lateinit var lightbutton:Button
    lateinit var hardbutton:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reco_op1)

        lightbutton=findViewById(R.id.lightbutton)
        hardbutton=findViewById(R.id.hardbutton)

        lightbutton.setOnClickListener {
            var intent= Intent(this, reco_op2::class.java)
            startActivity(intent)
        }
        hardbutton.setOnClickListener {
            var intent= Intent(this, reco_op2::class.java)
            startActivity(intent)
        }
    }
}