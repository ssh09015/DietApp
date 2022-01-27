package com.example.dietapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//activity_main.xml constraint 부분 오류나는 부분이랑 실행했을 때 이상한 부분들 변경했음(윤솔)
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var resultButton: Button//추후에 초기화 변수타입
    lateinit var heightEditText: EditText
    lateinit var weightEditText: EditText
    lateinit var navigationView : NavigationView
    lateinit var drawerLayout : DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setContentView(R.layout.navi_main)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            myStartActivity(SignUpActivity::class.java)
        } else {
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("users").document(user.uid)
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.data)
                        } else {
                            Log.d(TAG, "No such document")
                            myStartActivity(MemberInitActivity::class.java)
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.exception)
                }
            }
        }
        findViewById<View>(R.id.logoutButton).setOnClickListener(onClickListener)
        resultButton = findViewById<Button>(R.id.resultButton)
        heightEditText = findViewById<EditText>(R.id.heightEditText)
        weightEditText = findViewById<EditText>(R.id.weightEditText)

        loadData()

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)

        // 툴바를 액티비티의 앱바로 지정
        setSupportActionBar(toolbar)

        // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 홈버튼 이미지 변경
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        // 툴바에 타이틀 안보이게
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawer_layout)

        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this) // navigation 리스너

        resultButton.setOnClickListener {
            if (heightEditText.length()==0 && weightEditText.length()==0){ //키, 몸무게 값을 넣지 않았을 때 토스트 메시지 뜨기 부분(if부분만 세이가 넣고 else 안의 부분은 다른 분이 하셨음)
                Toast.makeText(this,"값을 모두 입력해주세요.",Toast.LENGTH_SHORT).show()
            }
            else{
                saveData(heightEditText.text.toString().toInt(), weightEditText.text.toString().toInt())
                //버튼이 눌릴때 동작
                var intent = Intent(this, ResultActivity::class.java) //bmi결과페이지로 이동
                intent.putExtra("height", heightEditText.text.toString()) //입력된 키값 가져오기
                intent.putExtra("weight", weightEditText.text.toString())
                startActivity(intent)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item!!.itemId){
            android.R.id.home -> { // 메뉴 버튼
                drawerLayout.openDrawer(GravityCompat.START) // 네비게이션 드로어 열기
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // navigation에서 각 아이템이 클릭되었을 때 할일
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_cal->{
                var intent=Intent(this, Cal::class.java)
                startActivity(intent)
            }
        }
        return false
    }

    // navigation이 열렸을 때 뒤로 가기 버튼을 누르면 navigation이 닫히게 하기
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    var onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.logoutButton -> {
                FirebaseAuth.getInstance().signOut()
                myStartActivity(SignUpActivity::class.java)
            }
        }
    }
    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        startActivity(intent)
    }
    companion object {
        private const val TAG = "MainActivity"
    }

    private fun saveData(height: Int, weight: Int){
        var pref = this.getPreferences(0)
        var editor = pref.edit()

        //editor.putString("KEY_NAME", nameEditText.text.toString()).apply()
        editor.putInt("KEY_HEIGHT", heightEditText.text.toString().toInt()).apply()
        editor.putInt("KEY_WEIGHT", weightEditText.text.toString().toInt()).apply()
    }

    private fun loadData(){
        var pref=this.getPreferences(0)
        //var name = pref.getString("KEY_NAME", "")
        var height = pref.getInt("KEY_HEIGHT", 0)
        var weight = pref.getInt("KEY_WEIGHT", 0)

        if(height != 0 && weight != 0){
            //nameEditText.setText(name.toString())
            heightEditText.setText(height.toString())
            weightEditText.setText(weight.toString())
        }
    }


   // override fun onCreateOptionsMenu(menu: Menu?): Boolean { //캘린더 메뉴(세이)
     //   menuInflater.inflate(R.menu.main,menu)
   //     return true
  //  }

   // override fun onOptionsItemSelected(item: MenuItem): Boolean { //캘린더 아이콘 누르면(세이)
   //     when(item?.itemId){
       //     R.id.action_cal->{
          //      var intent=Intent(this, Cal::class.java)
          //      startActivity(intent)
       //     }
     //  }
    //    return super.onOptionsItemSelected(item)
  //  }


}