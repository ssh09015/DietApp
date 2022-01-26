package com.example.dietapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        findViewById<View>(R.id.loginButton).setOnClickListener(onClickListener)
        findViewById<View>(R.id.gotoPasswordResetButton).setOnClickListener(onClickListener)
    }

    var onClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.loginButton -> login()
            R.id.gotoPasswordResetButton -> myStartActivity(PasswordResetActivity::class.java)
        }
    }

    private fun login() {
        val email = (findViewById<View>(R.id.emailEditText) as EditText).text.toString()
        val password = (findViewById<View>(R.id.passwordEditText) as EditText).text.toString()
        if (email.length > 0 && password.length > 0) {
            mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 이거 한번 확인해줘 이게 user을 한번도 사용하지 않는다고 경고떠서 깃허브에 안올라가서 우선 주석으로 바꿔놨어
                        val user = mAuth!!.currentUser
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

    private fun startToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //로그인 하고 뒤로 가기 버튼 누르면 앱 꺼지는 기능
        startActivity(intent)
    }
}