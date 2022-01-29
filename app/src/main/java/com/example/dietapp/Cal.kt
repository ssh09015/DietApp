package com.example.dietapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import java.io.FileInputStream
import java.io.FileOutputStream

class Cal : AppCompatActivity() { //달력 파트(세이)
    var userID: String = "userID"
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setContentView(R.layout.activity_cal)



        calendarView=findViewById(R.id.calendarView)
        diaryTextView=findViewById(R.id.diaryTextView)
        saveBtn=findViewById(R.id.saveBtn)
        deleteBtn=findViewById(R.id.deleteBtn)
        updateBtn=findViewById(R.id.updateBtn)
        diaryContent=findViewById(R.id.diaryContent)
        title=findViewById(R.id.title)
        contextEditText=findViewById(R.id.contextEditText)

        title.text = "Daily Memo"





        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)

        // 툴바를 액티비티의 앱바로 지정 (송하)
        setSupportActionBar(toolbar)
        // 드로어를 꺼낼 홈 버튼 활성화 (송하)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 홈버튼 (메뉴모양버튼으로) 이미지 변경 (송하)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        // 툴바에 타이틀 안보이게 (송하)
        supportActionBar?.setDisplayShowTitleEnabled(false)



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
            checkDay(year, month, dayOfMonth, userID)
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

    // 달력 내용 조회, 수정
    fun checkDay(cYear: Int, cMonth: Int, cDay: Int, userID: String) {
        //저장할 파일 이름설정
        fname = "" + userID + cYear + "-" + (cMonth + 1) + "" + "-" + cDay + ".txt"

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
        try {
            fileOutputStream = openFileOutput(readDay, MODE_NO_LOCALIZED_COLLATORS)
            val content = contextEditText.text.toString()
            fileOutputStream.write(content.toByteArray())
            fileOutputStream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}