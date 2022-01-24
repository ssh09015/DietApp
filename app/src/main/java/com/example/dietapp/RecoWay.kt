package com.example.dietapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView

class RecoWay : AppCompatActivity() {

    lateinit var video1 : ImageButton
    lateinit var video2 : ImageButton
    lateinit var video3 : ImageButton
    lateinit var text1: TextView
    lateinit var text2: TextView
    lateinit var text3: TextView
    lateinit var text4: TextView
    lateinit var layout : LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reco_way)

        video1 = findViewById(R.id.imageButton)
        video2 = findViewById(R.id.imageButton1)
        video3 = findViewById(R.id.imageButton2)
        text1 = findViewById(R.id.textView4)
        text2 = findViewById(R.id.textView2)
        text3 = findViewById(R.id.textView5)
        text4 = findViewById(R.id.textView6)
        layout = findViewById(R.id.recoLayout2)

        // ResultActivity에서 bmi 값 받기 (송하)
        var bmi = intent.getDoubleExtra("bmi", 0.0)
        var num = intent.getIntExtra("num", 0)

        // 추천 운동에서 동영상 부분을 누르면 추천 운동 영상의 유튜브 링크로 이동 (송하)
        when {
            // 정상 (유지)
            bmi >= 18.5 && num == 1 -> {
                text1.text = "과식하지 않기\n" + "물 많이 먹기"
                text2.text = "유산소(소미핏)"
                text3.text = "운동 전 스트레칭 (땅끄부부)"
                layout.visibility = android.view.View.INVISIBLE

                video1.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://youtu.be/lazFuEUBB7A")
                    startActivity(Intent.createChooser(intent, null))
                }
                    video2.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("https://youtu.be/2LyDkE7sDec")
                        startActivity(Intent.createChooser(intent, null))
                }
            }
            // 정상 (감량)
            bmi >= 18.5 && num == 2 -> {
                text1.text = "야식, 간식 줄이기\n" + "과식하지 않기\n" + "물 많이 먹기\n" + "식사 시간에 채소 먹는 양 늘리기"
                text2.text = "유산소 (빅씨스)"
                text3.text = "전신운동 (힘으뜸)"
                text4.text = "코어운동 (힘으뜸)"

                video1.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://youtu.be/sTX0C08SYBM")
                    startActivity(Intent.createChooser(intent, null))
                }

                video2.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://youtu.be/W8JpE-sdUAs")
                    startActivity(Intent.createChooser(intent, null))
                }

                video3.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://youtu.be/hJuO1AUqLUc")
                    startActivity(Intent.createChooser(intent, null))
                }
            }
            // 과체중 (유지)
            bmi >= 23 && num == 1 -> {
                    text1.text = "과식하지 않기\n" + "물 많이 먹기"
                    text2.text = "유산소(소미핏)"
                    text3.text = "운동 전 스트레칭 (땅끄부부)"
                    layout.visibility = android.view.View.INVISIBLE

                    video1.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("https://youtu.be/lazFuEUBB7A")
                        startActivity(Intent.createChooser(intent, null))
                    }

                    video2.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("https://youtu.be/2LyDkE7sDec")
                        startActivity(Intent.createChooser(intent, null))
                    }
            }
            // 과체중 (감량)
            bmi >= 23 && num == 2-> {
                text1.text = "야식, 간식 줄이기\n" + "과식하지 않기\n" + "물 많이 먹기\n" + "식사 시간에 채소 먹는 양 늘리기"
                text2.text = "유산소 (빅씨스)"
                text3.text = "전신운동 (힘으뜸)"
                text4.text = "코어운동 (힘으뜸)"

                video1.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://youtu.be/sTX0C08SYBM")
                    startActivity(Intent.createChooser(intent, null))
                }

                video2.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://youtu.be/W8JpE-sdUAs")
                    startActivity(Intent.createChooser(intent, null))
                }

                video3.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://youtu.be/hJuO1AUqLUc")
                    startActivity(Intent.createChooser(intent, null))
                }
            }
            // 1단계 비만
            bmi >= 25 -> {
                text1.text = "야식, 간식 줄이기\n" + "식사 시간에 채소 먹는 양 늘리기\n" + "물 많이 먹기"
                text2.text = "유산소 (빅씨스)"
                text3.text = "전신 스트레칭 (제이제이샬롱드핏)"
                layout.visibility = android.view.View.INVISIBLE

                video1.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://youtu.be/0IWibGOf1jU")
                    startActivity(Intent.createChooser(intent, null))
                }

                video2.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://youtu.be/sWCbpXfoU5A")
                    startActivity(Intent.createChooser(intent, null))
                }
            }
            // 2단계 비만
            bmi >= 30 -> {
                text1.text = "야식, 간식 줄이기\n" + "식사 시간에 채소 먹는 양 늘리기\n" + "물 많이 먹기"
                text2.text = "유산소 (힘으뜸)"
                text3.text = "걷기 유산소 (땅끄부부)"
                text4.text = "전신 스트레칭 (DanoTV)"

                video1.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://youtu.be/GQ_Dt7_Jfk8")
                    startActivity(Intent.createChooser(intent, null))
                }

                video2.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://youtu.be/t70t-sklypk")
                    startActivity(Intent.createChooser(intent, null))
                }

                video3.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://youtu.be/WwVLRtqGmzI")
                    startActivity(Intent.createChooser(intent, null))
                }
            }
            // 3단계 비만
            bmi >= 35 -> {
                text1.text = "야식, 간식 줄이기\n" + "식사 시간에 채소 먹는 양 늘리기\n" + "물 많이 먹기"
                text2.text = "유산소 (빅씨스)"
                text3.text = "전신 스트레칭 (DanoTV)"
                layout.visibility = android.view.View.INVISIBLE

                video1.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://youtu.be/myNjmnvI6x0")
                    startActivity(Intent.createChooser(intent, null))
                }

                video2.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://youtu.be/WwVLRtqGmzI")
                    startActivity(Intent.createChooser(intent, null))
                }
            }
            // 저체중
            else -> {
                text1.text = "하루 세번 규칙적으로 식사하기\n물 많이 먹기"
                text2.text = "유산소 (빅씨스)"
                text3.text = "운동 전 스트레칭 (소미핏)"
                text4.text = "타바타운동 (핏블리)"

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
    }
}