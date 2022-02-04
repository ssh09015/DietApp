package com.example.dietapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_info.*


// 회원 정보 보여주는 액티비티 (세이)
class UserInfoActivity : AppCompatActivity() {

    lateinit var nameTextView: TextView
    lateinit var phoneNumberTextView: TextView
    lateinit var birthDayTextView: TextView
    lateinit var addressTextView: TextView

    lateinit var correctButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        nameTextView=findViewById(R.id.nameTextView)
        phoneNumberTextView=findViewById(R.id.phoneNumberTextView)
        birthDayTextView=findViewById(R.id.birthDayTextView)
        addressTextView=findViewById(R.id.addressTextView)

        correctButton=findViewById(R.id.correctButton)

        // 회원정보 수정
        correctButton.setOnClickListener {
            myStartActivity(MemberInitActivity2::class.java)
        }

        // 탈퇴
        deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("회원 탈퇴")
            builder.setMessage("정말 탈퇴하시겠습니까?")
            builder.setPositiveButton(
                "취소"
            ) { _: DialogInterface?, _: Int -> }
            builder.setNegativeButton(
                "탈퇴"
            ) { _: DialogInterface?, _: Int ->
                // 파이어베이스 계정 삭제
                FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //로그아웃처리
                        FirebaseAuth.getInstance().signOut()
                        myStartActivity(SignUpActivity::class.java)
                        Toast.makeText(this, "탈퇴가 완료되었습니다", Toast.LENGTH_LONG).show()
                    } else {
                        // 회원가입 1시간 후면 재인증 해야 탈퇴 가능하기 때문에 재인증해야 함(파이어베이스 규칙)
                        val user = FirebaseAuth.getInstance().currentUser!!
                        val credential = EmailAuthProvider
                            .getCredential("user@example.com", "password1234")
                        user.reauthenticate(credential)
                            .addOnCompleteListener {
                                // 재인증 완료
                            }
                        user.delete()
                            .addOnCompleteListener { task ->
                                FirebaseAuth.getInstance().signOut()
                                myStartActivity(SignUpActivity::class.java)
                                Toast.makeText(this, "탈퇴가 완료되었습니다", Toast.LENGTH_LONG).show()
                            }
                    }
                }
            }
            builder.show()
        }

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)

        // 툴바를 액티비티의 앱바로 지정 (송하)
        setSupportActionBar(toolbar)
        // 드로어를 꺼낼 홈 버튼 활성화 (송하)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 홈버튼 (메뉴모양버튼으로) 이미지 변경 (송하)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        // 툴바에 타이틀 안보이게 (송하)
        supportActionBar?.setDisplayShowTitleEnabled(false)

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
                        nameTextView.text=document.data?.get("name").toString()  // 받아온 정보 텍스트뷰에 넣기
                        phoneNumberTextView.text=document.data?.get("phoneNumber").toString()
                        birthDayTextView.text=document.data?.get("birthDay").toString()
                        addressTextView.text=document.data?.get("address").toString()
                    } else {
                        Log.d(MainActivity.TAG, "No such document")
                    }
                }
            } else {
                Log.d(MainActivity.TAG, "get failed with ", task.exception)
            }
        }
    }

    // 화살표 누르면 메인화면으로 이동하는 함수 (송하)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item!!.itemId){
            android.R.id.home -> { // 메뉴 버튼
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun myStartActivity(c: Class<*>) { // 인텐트 이동 함수 따로 만듦듦
        val intent = Intent(this, c)
        startActivity(intent)
    }
}