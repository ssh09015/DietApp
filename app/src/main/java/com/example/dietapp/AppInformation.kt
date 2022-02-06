package com.example.dietapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem

class AppInformation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_information)
        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)

        // 툴바를 액티비티의 앱바로 지정
        setSupportActionBar(toolbar)
        // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 홈버튼 (화살표모양으로) 이미지 변경
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        // 툴바에 타이틀 안보이게
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    // 툴바에 화살표 버튼을 누르면 메인화면으로 이동
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            android.R.id.home -> {
                myStartActivity(MainActivity::class.java)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 인텐트 이동 함수
    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        startActivity(intent)
    }
}