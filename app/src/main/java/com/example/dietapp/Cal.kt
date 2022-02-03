package com.example.dietapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.FileInputStream
import java.io.FileOutputStream

// 달력 액티비티 (세이)
class
Cal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var userID:String="" // 스트링 타입 변수
    lateinit var fname: String //파일 이름
    lateinit var str: String //메모한 내용
    lateinit var calendarView: CalendarView //캘린더
    lateinit var updateBtn: Button //수정 버튼
    lateinit var deleteBtn:Button //삭제 버튼
    lateinit var saveBtn:Button //저장 버튼
    lateinit var diaryTextView: TextView //날짜 누르면 나오는 맨 위의 날짜 정보
    lateinit var diaryContent:TextView //메모하고 난 후 메모 영역
    lateinit var title:TextView //제목
    lateinit var contextEditText: EditText //사용자가 메모를 입력하는 영역
    lateinit var navigationView : NavigationView
    lateinit var drawerLayout : DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setContentView(R.layout.activity_cal)
        setContentView(R.layout.navi_cal)

        calendarView=findViewById(R.id.calendarView)
        diaryTextView=findViewById(R.id.diaryTextView)
        saveBtn=findViewById(R.id.saveBtn)
        deleteBtn=findViewById(R.id.deleteBtn)
        updateBtn=findViewById(R.id.updateBtn)
        diaryContent=findViewById(R.id.diaryContent)
        title=findViewById(R.id.title)
        contextEditText=findViewById(R.id.contextEditText)
        drawerLayout = findViewById(R.id.drawerLayoutCal)
        navigationView = findViewById(R.id.naviViewCal)

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
                        userID=document.data?.get("name").toString()  // 받아온 정보 텍스트뷰에 넣기
                        title.text=document.data?.get("name").toString() + "의 달력"// 달력 이름 바꾸기
                        navigationnameTextView.text = document.data?.get("name").toString() // 불러온 사용자 이름으로 텍스트뷰 바꾸기
                        navigationemailTextView.setText(user.email); // 사용자 이메일 불러오기
                        /*if(userID!=null){ // userID 값 들어왔는지 확인
                            startToast(title.toString())
                        }*/
                    } else {
                        Log.d(MainActivity.TAG, "No such document")
                    }
                }
            } else {
                Log.d(MainActivity.TAG, "get failed with ", task.exception)
            }
        }

        // 날짜 눌릴 때
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            diaryTextView.visibility = View.VISIBLE // 날짜 보이기
            saveBtn.visibility = View.VISIBLE // 저장 버튼 보이기
            contextEditText.visibility = View.VISIBLE // 입력할 영역 보이기

            diaryContent.visibility = View.INVISIBLE // 메모 내용이 보이던 칸 안보이게
            updateBtn.visibility = View.INVISIBLE // 수정 버튼 안 보이기
            deleteBtn.visibility = View.INVISIBLE // 삭제 버튼 안 보이기

            diaryTextView.text = String.format("%d / %d / %d", year, month + 1, dayOfMonth) //날짜 정보 가져오기
            contextEditText.setText("")
            checkDay(year, month, dayOfMonth, userID) // checkDay 함수에 넘기기
        }

        // 저장 버튼 눌릴 때
        saveBtn.setOnClickListener {
            saveDiary(fname)

            contextEditText.visibility = View.INVISIBLE
            saveBtn.visibility = View.INVISIBLE

            updateBtn.visibility = View.VISIBLE
            deleteBtn.visibility = View.VISIBLE
            str = contextEditText.text.toString()

            diaryContent.text = str
            diaryContent.visibility = View.VISIBLE
        }
    }

    // 로그인한 사용자를 알아보는 달력
    // 달력 내용 조회, 수정
    fun checkDay(cYear: Int, cMonth: Int, cDay: Int, userID: String) {
        //저장할 파일 이름설정
        fname = "" + userID + cYear + "-" + (cMonth + 1) + "" + "-" + cDay + ".txt" // 메모 내용 저장할 파일 이름 설정

        var fileInputStream: FileInputStream

        try {
            fileInputStream = openFileInput(fname) //파일을 읽기 모드로 오픈
            val fileData = ByteArray(fileInputStream.available())
            fileInputStream.read(fileData)
            fileInputStream.close()
            str = String(fileData) //파일 내용 받기
            contextEditText.visibility = View.INVISIBLE
            diaryContent.visibility = View.VISIBLE
            diaryContent.text = str // 파일 내용 들어있는 str
            saveBtn.visibility = View.INVISIBLE
            updateBtn.visibility = View.VISIBLE
            deleteBtn.visibility = View.VISIBLE

            // 수정 버튼 눌릴 때
            updateBtn.setOnClickListener {
                contextEditText.visibility = View.VISIBLE
                diaryContent.visibility = View.INVISIBLE
                contextEditText.setText(str)
                saveBtn.visibility = View.VISIBLE
                updateBtn.visibility = View.INVISIBLE
                deleteBtn.visibility = View.INVISIBLE
                diaryContent.text = contextEditText.text
            }

            // 삭제 버튼 눌릴 때
            deleteBtn.setOnClickListener {
                diaryContent.visibility = View.INVISIBLE
                updateBtn.visibility = View.INVISIBLE
                deleteBtn.visibility = View.INVISIBLE
                contextEditText.setText("") // 공백 만들기
                contextEditText.visibility = View.VISIBLE
                saveBtn.visibility = View.VISIBLE
                removeDiary(fname) // 파일 삭제
            }
            if (diaryContent.text == null) {
                diaryContent.visibility = View.INVISIBLE
                updateBtn.visibility = View.INVISIBLE
                deleteBtn.visibility = View.INVISIBLE
                diaryTextView.visibility = View.VISIBLE
                saveBtn.visibility = View.VISIBLE
                contextEditText.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace() // 에러 메세지 발생 근원지 찾아 단계별로 에러 출력
        }
    }


    // 달력 내용 제거
    @SuppressLint("WrongConstant")
    fun removeDiary(readDay: String?) {
        var fileOutputStream: FileOutputStream //파일에 쓰기
        try {
            fileOutputStream = openFileOutput(readDay, MODE_NO_LOCALIZED_COLLATORS)
            val content = ""
            fileOutputStream.write(content.toByteArray())
            fileOutputStream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    // 달력 내용 추가
    @SuppressLint("WrongConstant")

    fun saveDiary(readDay: String?) {
        var fileOutputStream: FileOutputStream

        fileOutputStream = openFileOutput(readDay, MODE_NO_LOCALIZED_COLLATORS)

        val content = contextEditText.text.toString()

        try {
            fileOutputStream = openFileOutput(readDay, MODE_NO_LOCALIZED_COLLATORS) // 파일에 쓰기
            val content = contextEditText.text.toString()
            fileOutputStream.write(content.toByteArray())
            fileOutputStream.close()

            if(content!=null){
                startToast(readDay.toString())
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    // navigation에서 각 아이템이 클릭되었을 때 할일 (송하)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                myStartActivity(MainActivity::class.java)
            }
            R.id.action_cal -> {
                startToast("여기가 캘린더 화면입니다.")
                drawerLayout.closeDrawers()

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
                // myStartActivity(UserInfoActivity::class.java)
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
