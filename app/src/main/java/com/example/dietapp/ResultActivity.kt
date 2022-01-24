package com.example.dietapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlin.math.round
import kotlin.math.roundToInt


class ResultActivity : AppCompatActivity() {

    //결과 페이지 부분 텍스트, 사진, 버튼 변수 선언(윤솔)
    lateinit var ResultTextView : TextView
    lateinit var imageView : ImageView
    lateinit var radioGroup: RadioGroup
    lateinit var radioButton1: RadioButton
    lateinit var radioButton2: RadioButton
    lateinit var progressBar : ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi_result)

        //변수에 위젯 대입(윤솔)
        ResultTextView = findViewById(R.id.bmiResultTextView)
        imageView = findViewById<ImageView>(R.id.imageView)
        radioGroup = findViewById(R.id.RadioGroup)
        radioButton1 = findViewById(R.id.radioButton2)
        radioButton2 = findViewById(R.id.radioButton1)
        progressBar = findViewById(R.id.progressBar)



        var height = intent.getStringExtra("height").toInt()
        var weight = intent.getStringExtra("weight").toInt()
        var num : Int = 0


        //BMI 계산_변수형 Double로 변경 (송하)
        var bmi : Double = weight / Math.pow(height/100.0, 2.0)

        //progressbar는 정수만 되는 관계로 bmi를 int형으로 변경 (윤솔)
        var bmiInt : Int = bmi.toInt()

        // 정상 몸무게 계산 (송하)
        var normalWeight : Double = 22.9 * Math.pow(height/100.0, 2.0)

        // 증량/감량 해야하는 몸무게 계산 (송하)
        var goalWeight : Double
        when {
            normalWeight > weight && bmi < 18.5 -> {
                goalWeight = normalWeight - weight
            }
            normalWeight < weight && bmi >= 23 -> {
                goalWeight = weight - normalWeight
            }
            else -> {
                goalWeight = 0.0
            }
        }
        goalWeight = round(goalWeight)

        //글자로 출력
        when {
            bmi >= 35 -> ResultTextView.text = "고도 비만"
            bmi >= 30 -> ResultTextView.text = "2단계 비만"
            bmi >= 25 -> ResultTextView.text = "1단계 비만"
            bmi >= 23 -> ResultTextView.text = "과체중"
            bmi >= 18.5 -> ResultTextView.text = "정상"
            else -> ResultTextView.text = "저체중"
        }

        //이미지로 출력
        when{
            bmi >= 23 ->
                imageView.setImageResource(
                    R.drawable.ic_baseline_sentiment_very_dissatisfied_24)
            bmi > 18.5 ->
                imageView.setImageResource(
                    R.drawable.ic_baseline_sentiment_satisfied_alt_24)
            else ->
                imageView.setImageResource(
                    R.drawable.ic_baseline_mood_bad_24)
        }

        //무게별 라디오 버튼 출력(윤솔)
        //송하가 말한 단계로 이름 수정 완료(윤솔)
        when{
            bmi >= 25 -> {  // 옵션 하나만 나와야 하는 부분에 두 개 나오는 부분 고쳐서 하나만 나오게 수정 함(세이)
                radioButton1.visibility = View.INVISIBLE
                radioButton2.text = "감량"
            }
            bmi >= 18.5 -> {
                radioButton1.text = "유지"
                radioButton2.text = "감량"
            }
            else -> {
                radioButton1.visibility = View.INVISIBLE
                radioButton2.text = "증량"
            }
        }

        //토스트 문구
        Toast.makeText(this,  "$bmi", Toast.LENGTH_SHORT).show()

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.radioButton1 -> {
                    num = 1
                }

                R.id.radioButton2 -> {
                    num = 2
                }
            }
            val intent = Intent(this, RecoWay::class.java)
            // bmi 값을 RecoWay로 전달 (송하)
            intent.putExtra("bmi", bmi)
            intent.putExtra("num", num)
            intent.putExtra("goalWeight", goalWeight)
            startActivity(intent)
            val intent2 = Intent(this, recommand::class.java)
            // bmi 값을 RecoWay로 전달 (송하)
            intent2.putExtra("bmi", bmi)
            intent2.putExtra("num", num)
            intent2.putExtra("goalWeight", goalWeight)
        }

        //progress 진행(max = 40이고 진행은 bmiInt 숫자로) progress 관련 수정은 activity_bmi_result에서 완료!!
            progressBar.max = 40
            progressBar.progress = bmiInt

    }

}