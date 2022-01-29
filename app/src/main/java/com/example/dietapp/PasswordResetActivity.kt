package com.example.dietapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

// 비밀번호 재설정 시 나오는 액티비티 (세이)
class PasswordResetActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null // 파이어베이스의 여러 가지 기능 사용 위한 FirebaseAuth 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        mAuth = FirebaseAuth.getInstance() // 파이어베이스 인증
        findViewById<View>(R.id.sendButton).setOnClickListener(onClickListener)
    }

    var onClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.sendButton -> send() // 보내기 버튼 눌렀을 때
        }
    }

    private fun send() {
        val email = (findViewById<View>(R.id.emailEditText) as EditText).text.toString()

        if (email.isNotEmpty()) {
            mAuth!!.sendPasswordResetEmail(email) // 회원 가입한 비밀번호 재설정 (FirebaseAuth의 기능)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startToast("이메일을 보냈습니다.")
                    }
                    else{
                        startToast("등록되지 않은 이메일입니다.")
                    }
                }
        } else {
            startToast("이메일을 입력해주세요.")
        }
    }

    // 토스트 메시지 함수 따로 만듦
    private fun startToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}