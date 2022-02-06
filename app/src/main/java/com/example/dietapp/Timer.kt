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
    companion object {
        private const val TAG = "MainActivity"
    }
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

        // 툴바를 액티비티의 앱바로 지정
        setSupportActionBar(toolbar)
        // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 홈버튼 (메뉴모양버튼으로) 이미지 변경
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        // 툴바에 타이틀 안보이게
        supportActionBar?.setDisplayShowTitleEnabled(false)
        // navigation 리스너
        navigationView.setNavigationItemSelectedListener(this)

        // navigation drawer header의 TextView를 파이어베이스에서 사용자 정보 불러와 바꾸기
        var navi_header=navigationView.getHeaderView(0)
        var navigationnameTextView=navi_header.findViewById<NavigationView>(R.id.navigationnameTextView) as TextView
        var navigationemailTextView=navi_header.findViewById<NavigationView>(R.id.navigationemailTextView) as TextView

        // 파이어베이스에 저장된 사용자 정보 불러오기 (파이어베이스 문서 참조)
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        val docRef = user?.let { db.collection("users").document(it.uid) }
        docRef?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {
                    if (document.exists()) { // 정보가 있으면
                        Log.d(TAG, "DocumentSnapshot data: " + document.data)
                        navigationnameTextView.text = document.data?.get("name").toString() // 불러온 사용자 이름으로 텍스트뷰 바꾸기
                        navigationemailTextView.setText(user.email); // 사용자 이메일 불러오기
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
            } else {
                Log.d(TAG, "get failed with ", task.exception)
            }
        }

        // 타이머 설정 버튼 누르면 변수에 입력한 시간 넣음
        settingButton.setOnClickListener {
            time = settingEditText.text.toString().toInt()*100
        }

        // 시작버튼
        startButton.setOnClickListener {
            // 시간 설정 안했을 때 토스트 메시지 띄우기
            if (time==0) {
                startToast("타이머 설정 먼저 해주세요.")
            }
            // 시간 설정 했으면 버튼 동작 실행
            else {
                isRunning = !isRunning

                // 타이머가 멈춰있으면 시작
                if (isRunning) {
                    start()
                }
                // 타이머가 실행중이면 중지
                else {
                    pause()
                }
            }
        }

        // 리셋버튼 누르면 타이머 리셋
        resetButton.setOnClickListener {
            reset()
        }
    }

    // 타이머 중지하는 함수
    private fun pause() {
        startButton.text = "START"
        timerTask?.cancel()
    }

    // 타이머 시작하는 함수
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
                    reset()
                }

            }
        }
    }

    // 타이머 리셋하는 함수
    private fun reset() {
        timerTask?.cancel()
        time = 0
        isRunning = false
        startButton.text="START"
        secTextView.text = "0"
        milliTextView.text = "00"
    }

    // 메뉴버튼 누르면 navigation Drawer 나오게 하는 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // navigation Drawer에서 각 아이템이 클릭되었을 때 할일
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // 메인 화면
            R.id.action_home -> {
                myStartActivity(MainActivity::class.java)
            }
            // 캘린더
            R.id.action_cal -> {
                myStartActivity(Cal::class.java)
            }
            // 회원정보
            R.id.action_account -> {
                myStartActivity(UserInfoActivity::class.java)
            }
            // 만보기
            R.id.action_walk -> {
                myStartActivity(StepActivity::class.java)
            }
            // 통계
            R.id.action_statics -> {
                myStartActivity(StatisticsActivity::class.java)
            }
            // 타이머
            R.id.action_timer -> {
                startToast("여기가 타이머 화면입니다.")
                drawerLayout.closeDrawers()
            }
            // 로그아웃
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut() // 사용자 로그아웃 시키는 signOut() (파이어베이스 참조)
                myStartActivity(SignUpActivity::class.java)
                startToast("로그아웃 되었습니다.")
            }
            // 앱 사용법
            R.id.action_manual -> {
                myStartActivity(AppManual::class.java)
            }
            // 앱정보
            R.id.action_information -> {
                myStartActivity(AppInformation::class.java)
            }
        }
        return false
    }

    // navigation Drawer가 열렸을 때 뒤로 가기 버튼을 누르면 navigation Drawer가 닫히게 하기
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            myStartActivity(MainActivity::class.java)
        }
    }

    // 토스트 버튼 함수
    private fun startToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    // 인텐트 이동 함수
    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        startActivity(intent)
    }
}