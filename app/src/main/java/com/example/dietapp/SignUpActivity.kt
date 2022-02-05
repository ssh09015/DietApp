package com.example.dietapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlin.system.exitProcess


// 회원가입 액티비티 (세이)
class SignUpActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null //파이어베이스 연동

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()
        findViewById<View>(R.id.signUpButton).setOnClickListener(onClickListener)
        findViewById<View>(R.id.gotoLoginButton).setOnClickListener(onClickListener)
    }

    // 뒤로 가기 버튼 눌렀을 때 앱 종료하기
    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
        Process.killProcess(Process.myPid())
        exitProcess(1)
    }

    var onClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.signUpButton -> signUp()
            R.id.gotoLoginButton -> myStartActivity(LoginActivity::class.java)
        }
    }

    private fun signUp() { // 회원가입 조건
        val email = (findViewById<View>(R.id.emailEditText) as EditText).text.toString()
        val password = (findViewById<View>(R.id.passwordEditText) as EditText).text.toString()
        val passwordCheck = (findViewById<View>(R.id.passwordCheckEditText) as EditText).text.toString()

        if (email.isNotEmpty() && password.isNotEmpty() && passwordCheck.isNotEmpty()) {
            if (password == passwordCheck) { // 비번과 비번 확인의 내용이 같으면
                mAuth!!.createUserWithEmailAndPassword(email, password) // 회원가입(파이어베이스에 계정 생성, FirebaseAuth의 기능)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = mAuth!!.currentUser
                            startToast("회원가입에 성공하였습니다.")
                            myStartActivity(MemberInitActivity::class.java)
                        } else {
                            if (task.exception != null) {
                                startToast(task.exception.toString())
                            }
                        }
                    }
            } else {
                startToast("비밀번호가 일치하지 않습니다.")
            }
        } else {
            startToast("이메일 또는 비밀번호를 입력해주세요.")
        }
    }

    // 토스트 메시지 따로 함수 만듦
    private fun startToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //로그인 하고 뒤로 가기 버튼 누르면 앱 꺼지는 기능
        startActivity(intent)
    }
}