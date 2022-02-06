package com.example.dietapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class PasswordResetActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)
        mAuth = FirebaseAuth.getInstance()
        findViewById<View>(R.id.sendButton).setOnClickListener(onClickListener)
    }

    // 버튼 클릭 시
    var onClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.sendButton -> send()
        }
    }

    // 비밀번호 재설정 이메일 보내기
    private fun send() {
        val email = (findViewById<View>(R.id.emailEditText) as EditText).text.toString()
        if (email.isNotEmpty()) {
            // 회원 가입한 비밀번호 재설정 (FirebaseAuth 기능)
            mAuth!!.sendPasswordResetEmail(email)
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

    // 토스트 메시지 함수
    private fun startToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}