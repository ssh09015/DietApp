package com.example.dietapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private var mAuth //추가
            : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance() //추가
        findViewById<View>(R.id.signUpButton).setOnClickListener(onClickListener)
        findViewById<View>(R.id.gotoLoginButton).setOnClickListener(onClickListener)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
        Process.killProcess(Process.myPid())
        System.exit(1)
    }

    var onClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.signUpButton -> signUp()
            R.id.gotoLoginButton -> myStartActivity(LoginActivity::class.java)
        }
    }

    private fun signUp() {
        val email = (findViewById<View>(R.id.emailEditText) as EditText).text.toString()
        val password = (findViewById<View>(R.id.passwordEditText) as EditText).text.toString()
        val passwordCheck = (findViewById<View>(R.id.passwordCheckEditText) as EditText).text.toString()
        if (email.length > 0 && password.length > 0 && passwordCheck.length > 0) {
            if (password == passwordCheck) {
                mAuth!!.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = mAuth!!.currentUser
                            startToast("회원가입에 성공하였습니다.")
                            myStartActivity(MainActivity::class.java)
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

    private fun startToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //로그인 하고 뒤로 가기 버튼 누르면 앱 꺼지는 기능
        startActivity(intent)
    }
}