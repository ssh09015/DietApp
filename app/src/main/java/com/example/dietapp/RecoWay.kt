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
    lateinit var goalText : TextView
    lateinit var goalText1 : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reco_way)
        video1 = findViewById(R.id.recoImage1)
        video2 = findViewById(R.id.recoImage2)
        video3 = findViewById(R.id.recoImage3)
        text1 = findViewById(R.id.recoText2)
        text2 = findViewById(R.id.recoText4)
        text3 = findViewById(R.id.recoText5)
        text4 = findViewById(R.id.recoText6)
        layout = findViewById(R.id.recoLayout)
        goalText = findViewById(R.id.goalText2)
        goalText1 = findViewById(R.id.goalText1)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)

        // 툴바를 액티비티의 앱바로 지정
        setSupportActionBar(toolbar)
        // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 홈버튼 (화살표모양으로) 이미지 변경
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        // 툴바에 타이틀 안보이게
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // ResultActivity에서 bmi 값 받기
        var bmi = intent.getDoubleExtra("bmi", 0.0)
        var num = intent.getIntExtra("num", 0)
        var weight = intent.getDoubleExtra("goalWeight", 0.0)

        // 추천 운동에서 동영상 부분을 누르면 추천 운동 영상의 유튜브 링크로 이동
        when {
            // 정상 (유지)
            bmi >= 18.5 && bmi < 23 && num == 2 -> {
                goalText1.visibility = android.view.View.INVISIBLE
                goalText.text = "정상 체중입니다!"
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
            bmi >= 18.5 && bmi < 23 && num == 1 -> {
                goalText1.visibility = android.view.View.INVISIBLE
                goalText.text = "정상 체중입니다!"
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
            bmi >= 23 && bmi < 25 && num == 2 -> {
                goalText.text = "$weight"+"kg 감량 필요!"
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
            bmi >= 23 && bmi < 25 && num == 1 -> {
                goalText.text = "$weight"+"kg 감량 필요!"
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
            bmi >= 25 && bmi < 30 -> {
                goalText.text = "$weight"+"kg 감량 필요!"
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
            bmi >= 30 && bmi < 35 -> {
                goalText.text = "$weight"+"kg 감량 필요!"
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
                goalText.text = "$weight"+"kg 감량 필요!"
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
                goalText.text = "$weight"+"kg 증량 필요!"
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