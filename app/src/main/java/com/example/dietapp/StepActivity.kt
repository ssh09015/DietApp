package com.example.dietapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StepActivity : AppCompatActivity(), SensorEventListener, NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private const val TAG = "MainActivity"
    }
    var sensorManager: SensorManager? = null
    var stepCountSensor: Sensor? = null
    var stepCountView: TextView? = null
    var resetButton: Button? = null
    lateinit var ctoastbutton: Button
    lateinit var navigationView : NavigationView
    lateinit var drawerLayout : DrawerLayout

    // 현재 걸음 수
    var currentSteps= 0

    // 현재 칼로리수
    var calorie:Double = 0.0

    @RequiresApi(api = Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step)
        setContentView(R.layout.navi_step)
        stepCountView = findViewById(R.id.stepCountView)
        resetButton=findViewById(R.id.resetButton)
        ctoastbutton=findViewById(R.id.ctoastbutton)
        drawerLayout = findViewById(R.id.drawerLayoutStep)
        navigationView = findViewById(R.id.naviViewStep)

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
        val docRef = user?.let { db.collection("users").document(it.uid) } // 사용자 고유 id로 불러오기
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

        // 리셋 버튼 누를 시
        resetButton?.setOnClickListener(View.OnClickListener {
            currentSteps=0
            stepCountView?.text=currentSteps.toString()
        })

        // 활동 퍼미션 체크
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 0)
        }

        // 걸음 센서 연결
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCountSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        // 디바이스에 걸음 센서의 존재 여부 체크
        if (stepCountSensor == null) {
            startToast("No Step Sensor")
        }

        //걸음 당 칼로리 계산 토스트 버튼
        ctoastbutton.setOnClickListener {
            var doubleStep=currentSteps.toDouble()
            calorie=0.04*doubleStep // kcal 단위로 바꿈
            startToast("${String.format("%.2f",calorie)}"+"kcal이 소모되었습니다.")
        }
    }

    public override fun onStart() {
        super.onStart()
        if (stepCountSensor != null) {
            sensorManager!!.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST) // 센서 속도 설정
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        // 걸음 센서 이벤트 발생시
        if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
            if (event.values[0] == 1.0f) {
                // 센서 이벤트가 발생할때 마다 걸음수 증가
                currentSteps++
                stepCountView!!.text = currentSteps.toString()
            }
        }
    }
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

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
            }// 만보기
            R.id.action_walk -> {
                startToast("여기가 만보기화면 입니다.")
                drawerLayout.closeDrawers()
            }
            // 통계
            R.id.action_statics -> {
                myStartActivity(StatisticsActivity::class.java)
            }
            // 타이머
            R.id.action_timer -> {
                myStartActivity(Timer::class.java)
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