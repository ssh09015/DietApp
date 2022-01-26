package com.example.dietapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MemberInitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_init)
        findViewById<View>(R.id.checkButton).setOnClickListener(onClickListener)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    var onClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.checkButton -> profileUpdate()
        }
    }

    private fun profileUpdate() {
        val name = (findViewById<View>(R.id.nameEditText) as EditText).text.toString()
        val phoneNumber = (findViewById<View>(R.id.phoneNumberEditText) as EditText).text.toString()
        val birthDay = (findViewById<View>(R.id.birthDayEditText) as EditText).text.toString()
        val address = (findViewById<View>(R.id.addressEditText) as EditText).text.toString()
        if (name.length > 0 && phoneNumber.length > 9 && birthDay.length > 5 && address.length > 0) {
            val user = FirebaseAuth.getInstance().currentUser
            val db = FirebaseFirestore.getInstance()
            val memberInfo = MemberInfo(name, phoneNumber, birthDay, address)
            if (user != null) {
                db.collection("users").document(user.uid).set(memberInfo)
                    .addOnSuccessListener {
                        startToast("회원정보 등록을 성공하였습니다.")
                        finish()
                    }
                    .addOnFailureListener { e ->
                        startToast("회원정보 등록에 실패하였습니다.")
                        Log.w(TAG, "Error writing document", e)
                    }
            }
        } else {
            startToast("회원정보를 입력해주세요.")
        }
    }

    private fun startToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MemberInitActivity"
    }
}