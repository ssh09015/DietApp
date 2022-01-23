package com.example.dietapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast



class ResultActivity : AppCompatActivity() {

    lateinit var ResultTextView : TextView
    lateinit var imageView : ImageView
    lateinit var radioGroup: RadioGroup
    lateinit var radioButton1: RadioButton
    lateinit var radioButton2: RadioButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi_result)

        ResultTextView = findViewById(R.id.bmiResultTextView)
        imageView = findViewById<ImageView>(R.id.imageView)
        radioGroup = findViewById(R.id.RadioGroup)
        radioButton1 = findViewById(R.id.radioButton2)
        radioButton2 = findViewById(R.id.radioButton1)



        var height = intent.getStringExtra("height").toInt()
        var weight = intent.getStringExtra("weight").toInt()


        //BMI 계산
        var bmi = weight / Math.pow(height/100.0, 2.0)
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

        when{
            bmi >= 23 -> {  // 옵션 하나만 나와야 하는 부분에 두 개 나오는 부분 고쳐서 하나만 나오게 수정 함(세이)
                radioButton1.visibility = View.INVISIBLE
                radioButton2.text = "살 빼기"
            }
            bmi > 18.5 -> {
                radioButton1.text = "유지하기"
                radioButton2.visibility = View.INVISIBLE
            }
            else -> {
                radioButton1.visibility = View.INVISIBLE
                radioButton2.text = "살 찌우기"
            }
        }

        Toast.makeText(this,  "$bmi", Toast.LENGTH_SHORT).show()

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.radioButton1 -> {
                    val intent = Intent(this, RecoWay::class.java)
                    startActivity(intent)
                }

                R.id.radioButton2 -> {
                    val intent = Intent(this, RecoWay::class.java)
                    startActivity(intent)
                }
            }
        }


    }
}