package com.example.dietapp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_member_init.*

class MemberInitActivity2 : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }
    private lateinit var dialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_init)

        // 회원 정보 입력
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
                            val storageRef=FirebaseStorage.getInstance().reference.child("/images/${document.data?.get("name").toString()}.jpg")
                            storageRef.delete().addOnSuccessListener {
                                Log.d(TAG,"파이어베이스 사진 삭제 완료")
                            }.addOnFailureListener {
                                Log.d(TAG,"파이어베이스 사진 삭제 실패"+document.data?.get("name").toString())
                            }
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

        // 확인 버튼
        checkButton.setOnClickListener {
            showProgressBar()
            uploadImageToFirebaseStorage()
        }

        // 프로필 사진 누를 때
        selectphoto_button_register.setOnClickListener {
            Log.d(TAG,"Try to show photo selector")
            val intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }
    }

    var selectedPhotoUri: Uri?=null

    // 사진 가져오는 부분
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0&&resultCode== Activity.RESULT_OK && data !=null){
            Log.d(TAG,"사진이 선택되었습니다.")
            selectedPhotoUri=data.data
            // uri를 ImageView로 바꾸는 과정
            val bitmap=MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            selectphoto_imageview_register.setImageBitmap(bitmap)
            selectphoto_button_register.alpha=0f
        }
    }

    // 파이어베이스에 사진 업로드
    private fun uploadImageToFirebaseStorage(){
        val name = (findViewById<View>(R.id.nameEditText) as EditText).text.toString()
        if(selectedPhotoUri==null) {
            hideProgressBar()
            startToast("이미지를 등록하세요.")
            return
        } else{
            val ref= FirebaseStorage.getInstance().getReference("/images/$name.jpg") // path 참조
            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")
                    ref.downloadUrl.addOnSuccessListener {
                        Log.d(TAG, "File Location: $it")
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
                .addOnFailureListener {
                    hideProgressBar()
                    Log.d(TAG, "Failed to upload image to storage: ${it.message}")
                }
        }
    }

    // 파이어베이스에 사용자 정보 저장
    private fun saveUserToFirebaseDatabase(profileImageUrl:String){
        val name = (findViewById<View>(R.id.nameEditText) as EditText).text.toString()
        val phoneNumber = (findViewById<View>(R.id.phoneNumberEditText) as EditText).text.toString()
        val birthDay = (findViewById<View>(R.id.birthDayEditText) as EditText).text.toString()
        val address = (findViewById<View>(R.id.addressEditText) as EditText).text.toString()
        val uid=FirebaseAuth.getInstance().uid?:""
        val ref= FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        // MemberInfo 클래스
        val memberInfo = MemberInfo(profileImageUrl, name, phoneNumber, birthDay, address)
        if (user != null) {
            ref.setValue(memberInfo)
                .addOnSuccessListener {
                    db.collection("users").document(user.uid).set(memberInfo)
                    startToast("회원정보를 수정하였습니다.")
                    hideProgressBar()
                    myStartActivity(MainActivity::class.java)
                    finish()
                }
                .addOnFailureListener {
                    startToast("회원정보 수정에 실패하였습니다.")
                    hideProgressBar()
                }
        }
    }

    // 뒤로 가기 버튼 누를 때
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    // 로딩창 보여주기
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

    // 토스트 메시지 함수
    private fun startToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    //로그인 하고 뒤로 가기 버튼 누르면 앱 꺼지는 기능
    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}