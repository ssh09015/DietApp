package com.example.dietapp

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dietapp.databinding.ActivityUserInfoBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_user_info.*
import java.io.File

// 회원 정보 보여주는 액티비티
class UserInfoActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }
    private lateinit var dialog:Dialog // 로딩창
    lateinit var binding: ActivityUserInfoBinding // 파이어베이스 이미지 가져와 바꾸기 위함
    lateinit var nameTextView: TextView // 이름
    lateinit var phoneNumberTextView: TextView // 전화번호
    lateinit var birthDayTextView: TextView // 생년월일
    lateinit var addressTextView: TextView // 주소
    lateinit var profileImage:ImageView // 프로필 사진 가져온 거 담을 이미지뷰 변수
    lateinit var correctButton: Button // 수정 버튼

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding:ActivityUserInfoBinding= ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        nameTextView=findViewById(R.id.nameTextView)
        phoneNumberTextView=findViewById(R.id.phoneNumberTextView)
        birthDayTextView=findViewById(R.id.birthDayTextView)
        addressTextView=findViewById(R.id.addressTextView)
        profileImage=findViewById(R.id.profileImage)
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
                showProgressBar()
                // 파이어베이스 계정 삭제
                // 파이어베이스에 저장된 사용자 정보 불러오기 (파이어베이스 문서 참조)
                val user = FirebaseAuth.getInstance().currentUser
                val db = FirebaseFirestore.getInstance()
                val docRef = user?.let { db.collection("users").document(it.uid) }
                docRef?.get()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null) {
                            if (document.exists()) { // 사진 먼저 삭제
                                val storageRef=FirebaseStorage.getInstance().reference.child("/images/${document.data?.get("name").toString()}.jpg")
                                storageRef.delete().addOnSuccessListener {
                                    Log.d(TAG,"firebase photo delete complete")
                                }.addOnFailureListener {
                                    Log.d(TAG,"firebase photo delete fail")
                                }
                                // 계정 삭제
                                FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener { task ->
                                    if (task.isSuccessful){
                                        db.collection("users").document(user.uid) // 문서 삭제
                                            .delete()
                                            .addOnSuccessListener {
                                                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                                                hideProgressBar()
                                                FirebaseAuth.getInstance().signOut()
                                                myStartActivity(SignUpActivity::class.java)
                                                startToast("탈퇴가 완료되었습니다")
                                            }
                                            .addOnFailureListener {
                                                    e -> Log.w(TAG, "Error deleting document", e)
                                            }
                                    }else {
                                        // 회원가입 1시간 후면 재인증 해야 탈퇴 가능하기 때문에 재인증해야 함(파이어베이스 규칙)
                                        val user=FirebaseAuth.getInstance().currentUser!!
                                        val credential = EmailAuthProvider
                                            .getCredential("user@example.com", "password1234")
                                        user.reauthenticate(credential)
                                            .addOnCompleteListener {
                                                // 재인증 완료시 계정 삭제
                                                user.delete()
                                                    .addOnCompleteListener { task ->
                                                        FirebaseAuth.getInstance().signOut()
                                                        myStartActivity(SignUpActivity::class.java)
                                                        startToast("탈퇴가 완료되었습니다")
                                                    }
                                            }
                                    }
                                }
                            } else {
                                Log.d(TAG, "No such document")
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.exception)
                    }
                }
            }
            builder.show()
        }

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)

        // 툴바를 액티비티의 앱바로 지정
        setSupportActionBar(toolbar)
        // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 홈버튼 (화살표모양으로) 이미지 변경
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        // 툴바에 타이틀 안보이게
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 파이어베이스에 저장된 사용자 정보 불러오기 (파이어베이스 문서 참조)
        showProgressBar()
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        val docRef = user?.let { db.collection("users").document(it.uid) }
        docRef?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {
                    if (document.exists()) { // 정보가 있으면
                        Log.d(TAG, "DocumentSnapshot data: " + document.data)
                        // 파이어베이스에서 사진 불러오기 (uri를 ImageView로 바꾸는 과정)
                        val storageRef=FirebaseStorage.getInstance().reference.child("/images/${document.data?.get("name").toString()}.jpg")
                        val localfile= File.createTempFile("Image","jpg")
                        storageRef.getFile(localfile).addOnSuccessListener {
                            val bitmap=BitmapFactory.decodeFile(localfile.absolutePath)
                            binding.profileImage.setImageBitmap(bitmap)
                            hideProgressBar()
                        }.addOnFailureListener{
                            startToast("사진을 불러오지 못했습니다.")
                            hideProgressBar()
                        }
                        // 받아온 정보 텍스트뷰에 넣기
                        nameTextView.text=document.data?.get("name").toString()
                        phoneNumberTextView.text=document.data?.get("phoneNumber").toString()
                        birthDayTextView.text=document.data?.get("birthDay").toString()
                        addressTextView.text=document.data?.get("address").toString()
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
            } else {
                Log.d(TAG, "get failed with ", task.exception)
            }
        }
    }

    // 화살표 누르면 메인화면으로 이동하는 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            android.R.id.home -> {
                myStartActivity(MainActivity::class.java)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 로딩창 보이기
    private fun showProgressBar(){
        dialog= Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    // 로딩창 숨기기
    private fun hideProgressBar(){
        dialog.dismiss()
    }

    // 토스트 버튼 함수
    private fun startToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    // 인텐트 이동 함수
    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        startActivity(intent)
    }
}