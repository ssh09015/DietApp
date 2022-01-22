package com.example.dietapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class RecoWay : AppCompatActivity() {

    lateinit var video : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reco_way)

        video = findViewById(R.id.imageButton)

        video.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://youtu.be/PRGLzLU3XCc")
            startActivity(Intent.createChooser(intent, null))
        }
    }
}