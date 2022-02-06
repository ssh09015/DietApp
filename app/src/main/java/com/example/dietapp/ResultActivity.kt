package com.example.dietapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlin.math.round


class ResultActivity : AppCompatActivity() {

    lateinit var ResultTextView : TextView
    lateinit var imageView : ImageView
    lateinit var radioGroup: RadioGroup
    lateinit var radioButton1: RadioButton
    lateinit var radioButton2: RadioButton
    lateinit var progressBar : ProgressBar
    lateinit var bmiButton: Button
    lateinit var myBmi: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi_result)

        ResultTextView = findViewById(R.id.bmiResultTextView)
        imageView = findViewById<ImageView>(R.id.manualImageView)
        radioGroup = findViewById(R.id.RadioGroup)
        radioButton1 = findViewById(R.id.bmiResultButton2)
        radioButton2 = findViewById(R.id.bmiResultButton1)
        progressBar = findViewById(R.id.progressBar)
        bmiButton = findViewById(R.id.bmiToastButton)
        myBmi = findViewById(R.id.myBmiTextView)



        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)

        // 툴바를 액티비티의 앱바로 지정
        setSupportActionBar(toolbar)
        // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 홈버튼 (화살표모양으로) 이미지 변경
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        // 툴바에 타이틀 안보이게
        supportActionBar?.setDisplayShowTitleEnabled(false)


        var height = intent.getStringExtra("height").toInt()
        var weight = intent.getStringExtra("weight").toInt()
        var num : Int = 0


        //BMI 계산_변수형 Double로 변경
        var bmi : Double = weight / Math.pow(height/100.0, 2.0)

        //progressbar는 정수만 되는 관계로 bmi를 int형으로 변경
        var bmiInt : Int = bmi.toInt()

        // 정상 몸무게 계산
        var normalWeight : Double = 22.9 * Math.pow(height/100.0, 2.0)

        // 증량/감량 해야하는 몸무게 계산
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
            bmi >= 35 -> ResultTextView.text = "'당신은 고도 비만입니다.'"
            bmi >= 30 -> ResultTextView.text = "'당신은 2단계 비만입니다.'"
            bmi >= 25 -> ResultTextView.text = "'당신은 1단계 비만입니다.'"
            bmi >= 23 -> ResultTextView.text = "'당신은 과체중입니다.'"
            bmi >= 18.5 -> ResultTextView.text = "'당신은 정상입니다.'"
            else -> ResultTextView.text = "'당신은 저체중입니다.'"
        }

        //이미지로 출력
        when{
            bmi >= 35 ->
                imageView.setImageResource(
                        R.drawable.high_obesity)
            bmi >= 30 ->
                imageView.setImageResource(
                        R.drawable.obesity2)
            bmi >= 25 ->
                imageView.setImageResource(
                        R.drawable.obesity1)
            bmi >= 23 ->
                imageView.setImageResource(
                    R.drawable.overweight)
            bmi > 18.5 ->
                imageView.setImageResource(
                    R.drawable.normal)
            else ->
                imageView.setImageResource(
                    R.drawable.underweight)
        }

        //무게별 라디오 버튼 출력
        when{
            bmi >= 25 -> {
                radioButton1.visibility = View.INVISIBLE
                radioButton2.text = "감량하기"
            }
            bmi >= 18.5 -> {
                radioButton1.text = "유지하기"
                radioButton2.text = "감량하기"
            }
            else -> {
                radioButton1.visibility = View.INVISIBLE
                radioButton2.text = "증량하기"
            }
        }

        // 라디오 버튼을 누르면 bmi 값을 RecoWay 페이지로 전달
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.bmiResultButton1 -> {
                    num = 1
                    val intent = Intent(this, RecoWay::class.java)
                    intent.putExtra("bmi", bmi)
                    intent.putExtra("num", num)
                    intent.putExtra("goalWeight", goalWeight)
                    startActivity(intent)
                }

                R.id.bmiResultButton2 -> {
                    num = 2
                    val intent = Intent(this, RecoWay::class.java)
                    intent.putExtra("bmi", bmi)
                    intent.putExtra("num", num)
                    intent.putExtra("goalWeight", goalWeight)
                    startActivity(intent)
                }
            }
        }

        //progress 진행(max = 40이고 진행은 bmiInt 숫자로)
            progressBar.max = 40
            progressBar.progress = bmiInt

        bmiButton.setOnClickListener {
            Toast.makeText(this,"키와 몸무게를 이용하여 지방의 양을 추정하는 비만 측정법",Toast.LENGTH_LONG).show()
        }

        myBmi.text = bmiInt.toString()

    }


}