package com.example.dietapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class RecoWay : AppCompatActivity() {

    lateinit var video1 : ImageButton
    lateinit var video2 : ImageButton
    lateinit var video3 : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reco_way)

        video1 = findViewById(R.id.imageButton)
        video2 = findViewById(R.id.imageButton1)
        video3 = findViewById(R.id.imageButton2)

        // 추천 운동에서 동영상 부분을 누르면 추천 운동 영상의 유튜브 링크로 이동 (송하)
        video1.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://youtu.be/PRGLzLU3XCc")
            startActivity(Intent.createChooser(intent, null))
        }

        video2.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://youtu.be/ahbAnkN4KJ0")
            startActivity(Intent.createChooser(intent, null))
        }

        video3.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://youtu.be/jfr6MtMB89A")
            startActivity(Intent.createChooser(intent, null))
        }
    }
}