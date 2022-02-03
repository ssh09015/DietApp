package com.example.dietapp

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


//activity_main.xml constraint 부분 오류나는 부분이랑 실행했을 때 이상한 부분들 변경했음(윤솔)
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener ,
    OnDialogCloseListner {
    lateinit var resultButton: Button //추후에 초기화 변수타입
    lateinit var heightEditText: EditText
    lateinit var weightEditText: EditText
    lateinit var navigationView : NavigationView
    lateinit var drawerLayout : DrawerLayout
    lateinit var todoRecyclerview: RecyclerView
    lateinit var addtdButton: Button
    lateinit var myDB: DataBaseHelper
    lateinit var mList: MutableList<ToDoModel>
    lateinit var adapter: ToDoAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setContentView(R.layout.navi_main)

        // 회원정보 입력(세이)
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) { // 현재 등록된 유저가 없을 때
            myStartActivity(SignUpActivity::class.java)
        } else { // 파이어베이스 정보 가져오기
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("users").document(user.uid) // 사용자 고유 id로 파이어베이스 정보 가져오기
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    // 회원 정보 있으면 안 뜨게
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
        resultButton = findViewById<Button>(R.id.resultButton)
        heightEditText = findViewById<EditText>(R.id.heightEditText)
        weightEditText = findViewById<EditText>(R.id.weightEditText)
        drawerLayout = findViewById(R.id.drawerLayoutMain)
        navigationView = findViewById(R.id.naviViewMain)


        loadData()


        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)

        // 툴바를 액티비티의 앱바로 지정 (송하)
        setSupportActionBar(toolbar)

        // 드로어를 꺼낼 홈 버튼 활성화 (송하)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 홈버튼 (메뉴모양버튼으로) 이미지 변경 (송하)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        // 툴바에 타이틀 안보이게 (송하)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        navigationView.setNavigationItemSelectedListener(this) // navigation 리스너 (송하)

        // navigation drawer header의 TextView를 파이어베이스에서 사용자 정보 불러와 바꾸기 (세이)
        var navi_header = navigationView.getHeaderView(0)
        var navigationnameTextView =
            navi_header.findViewById<NavigationView>(R.id.navigationnameTextView) as TextView // TextView로 바꾸기
        var navigationemailTextView =
            navi_header.findViewById<NavigationView>(R.id.navigationemailTextView) as TextView // TextView로 바꾸기

        if (user == null) { // 현재 등록된 유저가 없을 때
            myStartActivity(SignUpActivity::class.java)
        } else { // 파이어베이스 정보 가져오기
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("users").document(user.uid) // 사용자 고유 id로 파이어베이스 정보 가져오기
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.data)
                            navigationnameTextView.text =
                                document.data?.get("name").toString() // 불러온 사용자 이름으로 텍스트뷰 바꾸기
                            navigationemailTextView.setText(user.email); // 사용자 이메일 불러오기
                        } else {
                            Log.d(TAG, "No such document")
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.exception)
                }
            }
        }

        resultButton.setOnClickListener {
            if (heightEditText.length() == 0 && weightEditText.length() == 0) { //키, 몸무게 값을 넣지 않았을 때 토스트 메시지 뜨기 부분(if부분만 세이가 넣고 else 안의 부분은 다른 분이 하셨음)
                Toast.makeText(this, "값을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                saveData(
                    heightEditText.text.toString().toInt(),
                    weightEditText.text.toString().toInt()
                )
                //버튼이 눌릴때 동작
                var intent = Intent(this, ResultActivity::class.java) //bmi결과페이지로 이동
                intent.putExtra("height", heightEditText.text.toString()) //입력된 키값 가져오기
                intent.putExtra("weight", weightEditText.text.toString())
                startActivity(intent)
            }
        }

        //todolist 사용되는 변수들(지인)
        todoRecyclerview = findViewById(R.id.todoRecyclerview)
        addtdButton = findViewById(R.id.addtdButton)
        myDB = DataBaseHelper(this@MainActivity)
        //mList = MutableList()
        adapter = ToDoAdapter(myDB, this@MainActivity)
        todoRecyclerview.setHasFixedSize(true)
        todoRecyclerview.setLayoutManager(LinearLayoutManager(this))
        todoRecyclerview.setAdapter(adapter)
        mList = myDB.allTask
        //mList = myDB.allTask.toMutableList()
        Collections.reverse(mList)
        adapter.setTasks(mList)

        // 투두리스트 항목 스와이프 할 때 필요한 변수들 함수 (지인)
        val swipegesture = object :SwipeGesture(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.LEFT ->{
                        adapter.deleteTask(viewHolder.adapterPosition)
                    }
                    //ItemTouchHelper.RIGHT -> {
                    //}
                }
            }

        }
        val touchHelper = ItemTouchHelper(swipegesture)
        touchHelper.attachToRecyclerView(todoRecyclerview)


        // 투두리스트 추가 버튼 누르면 dialog 창 실행 (지인)
        addtdButton.setOnClickListener(View.OnClickListener { v: View? -> AddNewTask.newInstance().show(supportFragmentManager, AddNewTask.TAG) })
        //val itemTouchHelper = ItemTouchHelper(RecyclerViewTouchHelper(adapter))
        //itemTouchHelper.attachToRecyclerView(mRecyclerview)
    }


    // 투두리스트 추가버튼 누르면 나오는 창 (지인)
    override fun onDialogClose(dialogInterface: DialogInterface?) {
        mList = myDB.allTask.toMutableList()
        Collections.reverse(mList)
        adapter!!.setTasks(mList)
        adapter!!.notifyDataSetChanged()
    }




    // 메뉴바 누르면 네비게이션 기능 나오게 하는 함수 (송하)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item!!.itemId){
            android.R.id.home -> { // 메뉴 버튼
                drawerLayout.openDrawer(GravityCompat.START) // 네비게이션 드로어 열기
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // navigation에서 각 아이템이 클릭되었을 때 할일 (송하)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                Toast.makeText(this, "여기가 메인화면입니다.", Toast.LENGTH_SHORT).show()
                drawerLayout.closeDrawers()
            }
            R.id.action_cal -> {
                myStartActivity(Cal::class.java)
            }
            R.id.action_account -> {
                myStartActivity(UserInfoActivity::class.java)
            }
            R.id.action_walk -> {
                myStartActivity(StepActivity::class.java)
            }
            // 통계
            R.id.action_statics -> {
                myStartActivity(StatisticsActivity::class.java)
            }
            // 타이머
            R.id.action_timer -> {
                // 타이머로 이동
                myStartActivity(Timer::class.java)
            }
            R.id.action_logout -> {
                // 로그아웃 기능
                FirebaseAuth.getInstance().signOut() // 사용자 로그아웃 시키는 signOut() (파이어베이스 참조)
                myStartActivity(SignUpActivity::class.java)
                Toast.makeText(this,"로그아웃 되었습니다.", Toast.LENGTH_LONG).show()
            }
            R.id.action_information -> {
                // 앱정보 화면으로 이동
                myStartActivity(AppInformation::class.java)
            }
        }
        return false
    }

    // navigation이 열렸을 때 뒤로 가기 버튼을 누르면 navigation이 닫히게 하기 (송하)
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    private fun myStartActivity(c: Class<*>) { // 인텐트 이동을 따로 함수로 만듦 (세이)
        val intent = Intent(this, c)
        startActivity(intent)
    }
    companion object { // 태그 선언
        const val TAG = "MainActivity"
    }

    private fun saveData(height: Int, weight: Int){ // SharedPreference로 키, 몸무게 저장(세이)
        var pref = this.getPreferences(0)
        var editor = pref.edit()

        editor.putInt("KEY_HEIGHT", heightEditText.text.toString().toInt()).apply()
        editor.putInt("KEY_WEIGHT", weightEditText.text.toString().toInt()).apply()
    }

    private fun loadData(){
        var pref=this.getPreferences(0) // SharedPreference로 키, 몸무게 저장(세이)
        var height = pref.getInt("KEY_HEIGHT", 0)
        var weight = pref.getInt("KEY_WEIGHT", 0)

        if(height != 0 && weight != 0){
            heightEditText.setText(height.toString())
            weightEditText.setText(weight.toString())
        }
    }
}

