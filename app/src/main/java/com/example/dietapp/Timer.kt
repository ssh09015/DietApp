package com.example.dietapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Timer
import kotlin.concurrent.timer

class Timer : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var time = 0
    private var timerTask : Timer? = null
    private var isRunning = false

    lateinit var secTextView: TextView
    lateinit var milliTextView: TextView
    lateinit var settingEditText: EditText
    lateinit var settingButton : Button
    lateinit var navigationView : NavigationView
    lateinit var drawerLayout : DrawerLayout
    lateinit var startButton : Button
    lateinit var resetButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        setContentView(R.layout.navi_timer)

        secTextView = findViewById<TextView>(R.id.secTextView)
        milliTextView = findViewById<TextView>(R.id.milliTextView)
        settingEditText = findViewById<EditText>(R.id.settingEditText)
        settingButton = findViewById<Button>(R.id.settingButton)
        startButton = findViewById(R.id.startButton)
        resetButton = findViewById(R.id.resetButton)

        drawerLayout = findViewById(R.id.drawerLayoutTimer)
        navigationView = findViewById(R.id.naviViewTimer)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)

        // 툴바를 액티비티의 앱바로 지정 (송하)
        setSupportActionBar(toolbar)

        // 드로어를 꺼낼 홈 버튼 활성화 (송하)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 홈버튼 (메뉴모양버튼으로) 이미지 변경 (송하)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
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

        settingButton.setOnClickListener {
            time = settingEditText.text.toString().toInt()*100
        }

        startButton.setOnClickListener {

            if (time==0) {
            Toast.makeText(this, "타이머 설정을 해주세요.",Toast.LENGTH_SHORT).show()
            }
            else {
                isRunning = !isRunning

                if (isRunning) {
                    start()
                } else {
                    pause()
                }
            }
        }

        resetButton.setOnClickListener {
            reset()
        }

    }

    private fun pause() {
        startButton.text = "START"
        timerTask?.cancel()
    }


    private fun start() {
        startButton.text = "PAUSE"

        timerTask = timer(period=10){
            time--
            val sec = time / 100
            val milli = time % 100

            if (time == 0) {
                timerTask?.cancel()
            }

            runOnUiThread {
                if (time != 0) {
                    secTextView.text = "$sec"
                    milliTextView.text = "$milli"
                } else {
                    secTextView.text = "0"
                    milliTextView.text = "00"
                }

            }
        }
    }

    private fun reset() {
        timerTask?.cancel()

        time = 0
        isRunning = false
        startButton.text="START"
        secTextView.text = "0"
        milliTextView.text = "00"
    }

    // 메뉴바 누르면 네비게이션 기능 나오게 하는 함수 (송하)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item!!.itemId){
            android.R.id.home -> { // 메뉴 버튼
                drawerLayout.openDrawer(GravityCompat.START) // 네비게이션 드로어 열기
            }
        }

        return super.onOptionsItemSelected(item)
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
            R.id.action_walk -> {
                myStartActivity(StepActivity::class.java)
            }
            // 통계
            R.id.action_statics -> {
                myStartActivity(StatisticsActivity::class.java)
            }
            // 타이머
            R.id.action_timer -> {
                // 타이머로 이동
                Toast.makeText(this, "여기가 타이머 화면입니다.", Toast.LENGTH_SHORT).show()
                drawerLayout.closeDrawers()

            }
            R.id.action_logout -> {
                // 로그아웃 기능
                FirebaseAuth.getInstance().signOut() // 사용자 로그아웃 시키는 signOut() (파이어베이스 참조)
                myStartActivity(SignUpActivity::class.java)
                Toast.makeText(this,"로그아웃 되었습니다.", Toast.LENGTH_LONG).show()
            }
            R.id.action_manual -> {
                //앱 사용 방법 화면으로 이동
                myStartActivity(AppManual::class.java)
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
            myStartActivity(MainActivity::class.java)
        }
    }

    private fun myStartActivity(c: Class<*>) { // 인텐트 이동을 따로 함수로 만듦 (세이)
        val intent = Intent(this, c)
        startActivity(intent)
    }
}