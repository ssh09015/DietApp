package com.example.dietapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MemberInitActivity2 : AppCompatActivity() {
    private val TAG = "MainActivity" // 태그  (세이)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_init)

        findViewById<View>(R.id.checkButton).setOnClickListener(onClickListener) // 확인 버튼 누를 때 (세이)

        // 회원 정보 입력 (세이)
        val user = FirebaseAuth.getInstance().currentUser // 현재 사용자 가져옴
        if (user == null) { // 만약 로그인이 안 되어 있다면
            myStartActivity(SignUpActivity::class.java) // 회원가입 창 먼저 뜨기
        } else {
            val db = FirebaseFirestore.getInstance() // 파이어베이스에서 정보 가져오기 (파이어베이스 문서 참조)
            val docRef = db.collection("users").document(user.uid) // 고유 id로 사용자 정보 가져오기
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.data)
                        } else {
                            Log.d(TAG, "No such document")
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.exception)
                }
            }
        }
    }

    override fun onBackPressed() { // 뒤로 가기 버튼 누를 때(세이)
        super.onBackPressed()
        finish() // 앱 종료
    }

    var onClickListener = View.OnClickListener { v -> // 확인 버튼 클릭 시 (세이)
        when (v.id) {
            R.id.checkButton -> profileUpdate()
        }
    }

    // 파이어베이스에 프로필 정보(이름, 전화번호, 생년월일, 주소) 업데이트(세이)
    private fun profileUpdate() {
        val name = (findViewById<View>(R.id.nameEditText) as EditText).text.toString()
        val phoneNumber = (findViewById<View>(R.id.phoneNumberEditText) as EditText).text.toString()
        val birthDay = (findViewById<View>(R.id.birthDayEditText) as EditText).text.toString()
        val address = (findViewById<View>(R.id.addressEditText) as EditText).text.toString()

        if (name.isNotEmpty() && phoneNumber.length > 9 && birthDay.length > 5 && address.isNotEmpty()) { // 최소 입력 수
            val user = FirebaseAuth.getInstance().currentUser // 사용자 정보 가져오기 (파이어베이스 문서 참조)
            val db = FirebaseFirestore.getInstance()
            val memberInfo = MemberInfo(name, phoneNumber, birthDay, address)

            if (user != null) {
                db.collection("users").document(user.uid).set(memberInfo)
                        .addOnSuccessListener {
                            startToast("회원정보를 수정했습니다.")
                            myStartActivity(MainActivity::class.java)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            startToast("회원정보 수정에 실패했습니다.")
                            Log.w(TAG, "Error writing document", e)
                        }
            }
        } else {
            startToast("회원정보를 입력해주세요.")
        }
    }

    private fun startToast(msg: String) { // 토스트 메시지 따로 함수 만들어놓음(세이)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MemberInitActivity"
    }

    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //로그인 하고 뒤로 가기 버튼 누르면 앱 꺼지는 기능
        startActivity(intent)
    }
}
