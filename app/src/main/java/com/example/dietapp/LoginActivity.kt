package com.example.dietapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

// 로그인 액티비티
class LoginActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null // 파이어베이스 연동

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance() // 파이어베이스 인증
        findViewById<View>(R.id.loginButton).setOnClickListener(onClickListener)
        findViewById<View>(R.id.gotoPasswordResetButton).setOnClickListener(onClickListener)
    }

    var onClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.loginButton -> login()
            R.id.gotoPasswordResetButton -> myStartActivity(PasswordResetActivity::class.java) // 비밀번호 재설정 액티비티로 이동
        }
    }

    private fun login() { // 로그인 함수. 이메일, 비번 받기
        val email = (findViewById<View>(R.id.emailEditText) as EditText).text.toString()
        val password = (findViewById<View>(R.id.passwordEditText) as EditText).text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            mAuth!!.signInWithEmailAndPassword(email, password) // 로그인 (FirebaseAuth의 기능)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = mAuth!!.currentUser // 현재 로그인된 유저 정보
                            startToast("로그인에 성공하였습니다.")
                            myStartActivity(MainActivity::class.java)
                        } else {
                            if (task.exception != null) {
                                startToast(task.exception.toString())
                            }
                        }
                    }
        } else {
            startToast("이메일 또는 비밀번호를 입력해주세요.")
        }
    }

    // 토스트 메시지
    private fun startToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    // 다른 액티비티로 이동
    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //로그인 하고 뒤로 가기 버튼 누르면 앱 꺼지는 기능
        startActivity(intent)
    }
}
