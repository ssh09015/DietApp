package com.example.dietapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.round


class ResultActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //결과 페이지 부분 텍스트, 사진, 버튼 변수 선언(윤솔)
    lateinit var ResultTextView : TextView
    lateinit var imageView : ImageView
    lateinit var radioGroup: RadioGroup
    lateinit var radioButton1: RadioButton
    lateinit var radioButton2: RadioButton
    lateinit var progressBar : ProgressBar
    lateinit var bmiButton: Button
    lateinit var myBmi: TextView
    lateinit var navigationView : NavigationView
    lateinit var drawerLayout : DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi_result)
        setContentView(R.layout.navi_result)

        //변수에 위젯 대입(윤솔)
        ResultTextView = findViewById(R.id.bmiResultTextView)
        imageView = findViewById<ImageView>(R.id.imageView)
        radioGroup = findViewById(R.id.RadioGroup)
        radioButton1 = findViewById(R.id.bmiResultButton2)
        radioButton2 = findViewById(R.id.bmiResultButton1)
        progressBar = findViewById(R.id.progressBar)
        bmiButton = findViewById(R.id.bmiToastButton)
        myBmi = findViewById(R.id.myBmiTextView)
        drawerLayout = findViewById(R.id.drawerLayoutResult)
        navigationView = findViewById(R.id.naviViewResult)


        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)

        // 툴바를 액티비티의 앱바로 지정 (송하)
        setSupportActionBar(toolbar)
        // 드로어를 꺼낼 홈 버튼 활성화 (송하)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 홈버튼 (메뉴모양버튼으로) 이미지 변경 (송하)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        // 툴바에 타이틀 안보이게 (송하)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        navigationView.setNavigationItemSelectedListener(this) // navigation 리스너 (송하)

        // navigation drawer header의 TextView를 파이어베이스에서 사용자 정보 불러와 바꾸기 (세이)
        var navi_header=navigationView.getHeaderView(0)
        var navigationnameTextView=navi_header.findViewById<NavigationView>(R.id.navigationnameTextView) as TextView // TextView로 바꾸기
        var navigationemailTextView=navi_header.findViewById<NavigationView>(R.id.navigationemailTextView) as TextView // TextView로 바꾸기

        // 파이어베이스에 저장된 사용자 정보 불러오기 (파이어베이스 문서 참조)
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        val docRef = user?.let { db.collection("users").document(it.uid) } // 사용자 고유 id로 불러오기
        docRef?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {
                    if (document.exists()) { // 정보가 있으면
                        Log.d(MainActivity.TAG, "DocumentSnapshot data: " + document.data)
                        navigationnameTextView.text = document.data?.get("name").toString() // 불러온 사용자 이름으로 텍스트뷰 바꾸기
                        navigationemailTextView.setText(user.email); // 사용자 이메일 불러오기
                    } else {
                        Log.d(MainActivity.TAG, "No such document")
                    }
                }
            } else {
                Log.d(MainActivity.TAG, "get failed with ", task.exception)
            }
        }

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
            bmi >= 35 -> ResultTextView.text = "'당신은 고도 비만입니다.'"
            bmi >= 30 -> ResultTextView.text = "'당신은 2단계 비만입니다.'"
            bmi >= 25 -> ResultTextView.text = "'당신은 1단계 비만입니다.'"
            bmi >= 23 -> ResultTextView.text = "'당신은 과체중입니다.'"
            bmi >= 18.5 -> ResultTextView.text = "'당신은 정상입니다.'"
            else -> ResultTextView.text = "'당신은 저체중입니다.'"
        }

        //이미지로 출력 변경했습니다.(윤솔)
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

        //무게별 라디오 버튼 출력(윤솔)
        //송하가 말한 단계로 이름 수정 완료(윤솔)
        when{
            bmi >= 25 -> {  // 옵션 하나만 나와야 하는 부분에 두 개 나오는 부분 고쳐서 하나만 나오게 수정 함(세이)
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

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.bmiResultButton1 -> {
                    num = 1
                    val intent = Intent(this, RecoWay::class.java)
                    // bmi 값을 RecoWay로 전달 (송하)
                    intent.putExtra("bmi", bmi)
                    intent.putExtra("num", num)
                    intent.putExtra("goalWeight", goalWeight)
                    startActivity(intent)
                }

                R.id.bmiResultButton2 -> {
                    num = 2
                    val intent = Intent(this, RecoWay::class.java)
                    // bmi 값을 RecoWay로 전달 (송하)
                    intent.putExtra("bmi", bmi)
                    intent.putExtra("num", num)
                    intent.putExtra("goalWeight", goalWeight)
                    startActivity(intent)
                }
            }
        }

        //progress 진행(max = 40이고 진행은 bmiInt 숫자로) progress 관련 수정은 activity_bmi_result에서 완료!!
            progressBar.max = 40
            progressBar.progress = bmiInt

        bmiButton.setOnClickListener {
            Toast.makeText(this,"키와 몸무게를 이용하여 지방의 양을 추정하는 비만 측정법",Toast.LENGTH_LONG).show()
        }

        myBmi.text = bmiInt.toString()

    }

    // navigation에서 각 아이템이 클릭되었을 때 할일 (송하)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                myStartActivity(MainActivity::class.java)
            }
            R.id.action_cal -> {
                myStartActivity(Cal::class.java)
            }
            R.id.action_account -> {
                myStartActivity(UserInfoActivity::class.java)
            }
            R.id.action_logout -> {
                // 로그아웃 기능
                FirebaseAuth.getInstance().signOut() // 사용자 로그아웃 시키는 signOut() (파이어베이스 참조)
                myStartActivity(SignUpActivity::class.java)
                startToast("로그아웃 되었습니다.")
            }
            R.id.action_information -> {
                // 앱정보 화면으로 이동
                myStartActivity(AppInformation::class.java)
            }
        }
        return false
    }

    // navigation이 열렸을 때 뒤로 가기 버튼을 누르면 navigation이 닫히게 하기 (송하)
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    // 토스트 메시지 따로 함수 만듦
    private fun startToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun myStartActivity(c: Class<*>) { // 세이가 메인화면에 만든 인텐트 이동 함수 가져옴 (송하)
        val intent = Intent(this, c)
        startActivity(intent)
    }
}