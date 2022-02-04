package com.example.dietapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_member_init.*
import java.util.*

class MemberInitActivity2 : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_init)

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
        checkButton.setOnClickListener {
            uploadImageToFirebaseStorage()
        }

        selectphoto_button_register.setOnClickListener {
            Log.d(TAG,"Try to show photo selector")

            val intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }
    }


    var selectedPhotoUri: Uri?=null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==0&&resultCode== Activity.RESULT_OK && data !=null){
            // proceed and check what the selected image was....
            Log.d(TAG,"사진이 선택되었습니다.")

            selectedPhotoUri=data.data

            val bitmap=MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_button_register.alpha=0f
        }
    }

    private fun uploadImageToFirebaseStorage(){
        val name = (findViewById<View>(R.id.nameEditText) as EditText).text.toString()
        if(selectedPhotoUri==null) return

        val user = FirebaseAuth.getInstance().currentUser
        val ref= FirebaseStorage.getInstance().getReference("/images/$name.jpg")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl:String){
        val name = (findViewById<View>(R.id.nameEditText) as EditText).text.toString()
        val phoneNumber = (findViewById<View>(R.id.phoneNumberEditText) as EditText).text.toString()
        val birthDay = (findViewById<View>(R.id.birthDayEditText) as EditText).text.toString()
        val address = (findViewById<View>(R.id.addressEditText) as EditText).text.toString()

        val uid=FirebaseAuth.getInstance().uid?:""
        val ref= FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = FirebaseAuth.getInstance().currentUser // 사용자 정보 가져오기 (파이어베이스 문서 참조)
        val db = FirebaseFirestore.getInstance()
        val memberInfo = MemberInfo(profileImageUrl, name, phoneNumber, birthDay, address)

        if (user != null) {
            ref.setValue(memberInfo)
                .addOnSuccessListener {
                    db.collection("users").document(user.uid).set(memberInfo)
                    var intent=Intent(this,UserInfoActivity::class.java)
                    intent.putExtra("name",name)
                    startToast("회원정보를 수정하였습니다.")
                    myStartActivity(MainActivity::class.java)
                    finish()
                    //Log.d(TAG, "Finally we saved the user to Firebase Database")
                }
                .addOnFailureListener {
                    startToast("회원정보 수정에 실패하였습니다.")
                    //Log.d(TAG, "Failed to set value to database: ${it.message}")
                }
        }




        /*ref.setValue(user)
            .addOnSuccessListener {
                startToast("회원정보 등록을 성공하였습니다.")
                myStartActivity(MainActivity::class.java)
                finish()
                //Log.d(TAG, "Finally we saved the user to Firebase Database")
            }
            .addOnFailureListener {
                startToast("회원정보 등록에 실패하였습니다.")
                //Log.d(TAG, "Failed to set value to database: ${it.message}")
            }*/

    }















    override fun onBackPressed() { // 뒤로 가기 버튼 누를 때(세이)
        super.onBackPressed()
        finish() // 앱 종료
    }

    /*// 파이어베이스에 프로필 정보(이름, 전화번호, 생년월일, 주소) 업데이트(세이)
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
                            startToast("회원정보 등록을 성공하였습니다.")
                            myStartActivity(MainActivity::class.java)
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
    }*/

    private fun startToast(msg: String) { // 토스트 메시지 따로 함수 만들어놓음(세이)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //로그인 하고 뒤로 가기 버튼 누르면 앱 꺼지는 기능
        startActivity(intent)
    }
}
