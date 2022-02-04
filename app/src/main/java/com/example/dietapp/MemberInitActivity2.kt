package com.example.dietapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_member_init.*

// 수정 버튼 눌릴 때 액티비티 (MemberInitActivity에서 토스트 부분만 달리했음. 자세한 부분은 MemberInitActivity 참고)
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
                    startToast("회원정보를 수정하였습니다.")
                    myStartActivity(MainActivity::class.java)
                    finish()
                }
                .addOnFailureListener {
                    startToast("회원정보 수정에 실패하였습니다.")
                }
        }
    }

    // 뒤로 가기 버튼 누를 때(세이)
    override fun onBackPressed() {
        super.onBackPressed()
        finish() // 앱 종료
    }

    // 토스트 메시지 따로 함수 만들어놓음(세이)
    private fun startToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //로그인 하고 뒤로 가기 버튼 누르면 앱 꺼지는 기능
        startActivity(intent)
    }
}
