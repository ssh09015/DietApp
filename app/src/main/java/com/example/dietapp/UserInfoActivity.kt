package com.example.dietapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// 회원 정보 보여주는 액티비티 (세이)
class UserInfoActivity : AppCompatActivity() {

    lateinit var nameTextView: TextView
    lateinit var phoneNumberTextView: TextView
    lateinit var birthDayTextView: TextView
    lateinit var addressTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        nameTextView=findViewById(R.id.nameTextView)
        phoneNumberTextView=findViewById(R.id.phoneNumberTextView)
        birthDayTextView=findViewById(R.id.birthDayTextView)
        addressTextView=findViewById(R.id.addressTextView)


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

    private fun myStartActivity(c: Class<*>) { // 인텐트 이동 함수 따로 만듦듦        val intent = Intent(this, c)
        startActivity(intent)
    }
}