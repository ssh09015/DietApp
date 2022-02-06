package com.example.dietapp

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
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
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener ,
    OnDialogCloseListner {
    companion object {
        const val TAG = "MainActivity"
    }
    lateinit var resultButton: Button
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

        // 회원정보 입력
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) { // 현재 등록된 유저가 없을 때
            myStartActivity(SignUpActivity::class.java)
        } else {
            // 파이어베이스 정보 가져오기
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("users").document(user.uid) // 사용자 고유 id로 파이어베이스 정보 가져오기
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
        resultButton = findViewById<Button>(R.id.resultButton)
        heightEditText = findViewById<EditText>(R.id.heightEditText)
        weightEditText = findViewById<EditText>(R.id.weightEditText)
        drawerLayout = findViewById(R.id.drawerLayoutMain)
        navigationView = findViewById(R.id.naviViewMain)
        todoRecyclerview = findViewById(R.id.todoRecyclerview)
        addtdButton = findViewById(R.id.addtdButton)

        loadData()

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        // 툴바를 액티비티의 앱바로 지정
        setSupportActionBar(toolbar)
        // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 홈버튼 (메뉴모양버튼으로) 이미지 변경
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        // 툴바에 타이틀 안보이게
        supportActionBar?.setDisplayShowTitleEnabled(false)
        // navigation 리스너
        navigationView.setNavigationItemSelectedListener(this)

        // navigation drawer header의 TextView를 파이어베이스에서 사용자 정보 불러와 바꾸기
        var navi_header = navigationView.getHeaderView(0)
        var navigationnameTextView = navi_header.findViewById<NavigationView>(R.id.navigationnameTextView) as TextView
        var navigationemailTextView = navi_header.findViewById<NavigationView>(R.id.navigationemailTextView) as TextView

        // 현재 등록된 유저가 없을 때
        if (user == null) {
            myStartActivity(SignUpActivity::class.java)
        } else {
            // 파이어베이스 정보 가져오기
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("users").document(user.uid) // 사용자 고유 id로 파이어베이스 정보 가져오기
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.data)
                            navigationnameTextView.text = document.data?.get("name").toString() // 불러온 사용자 이름으로 텍스트뷰 바꾸기
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

        // 입력 버튼 누를 시
        resultButton.setOnClickListener {
            //키, 몸무게 값을 넣지 않았을 때 토스트 메시지 뜨기 부분
            if (heightEditText.length() == 0 && weightEditText.length() == 0) {
                startToast("값을 모두 입력해주세요.")
            } else {
                saveData(
                    heightEditText.text.toString().toInt(),
                    weightEditText.text.toString().toInt()
                )
                var intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("height", heightEditText.text.toString())
                intent.putExtra("weight", weightEditText.text.toString())
                startActivity(intent)
            }
        }

        myDB = DataBaseHelper(this@MainActivity)
        adapter = ToDoAdapter(myDB, this@MainActivity)
        todoRecyclerview.setHasFixedSize(true)
        todoRecyclerview.setLayoutManager(LinearLayoutManager(this))
        todoRecyclerview.setAdapter(adapter)
        mList = myDB.allTask
        Collections.reverse(mList)
        adapter.setTasks(mList)

        // 투두리스트 항목 스와이프 할 때 필요한 변수들 함수
        val swipegesture = object :SwipeGesture(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.LEFT ->{
                        adapter.deleteTask(viewHolder.adapterPosition)
                    }
                }
            }
        }
        // 투두리스트 항목들의 스와이프 제스쳐를 시행해주는 함수
        val touchHelper = ItemTouchHelper(swipegesture)
        touchHelper.attachToRecyclerView(todoRecyclerview)

        // 투두리스트 추가 버튼 누르면 dialog 창 실행
        addtdButton.setOnClickListener(View.OnClickListener { v: View? -> AddNewTask.newInstance().show(supportFragmentManager, AddNewTask.TAG) })
    }

    // 투두리스트 dialog창에서 저장버튼을 누르고 창이 사라지면 실행되는 함수
    override fun onDialogClose(dialogInterface: DialogInterface?) {
        mList = myDB.allTask.toMutableList()
        Collections.reverse(mList)
        adapter!!.setTasks(mList)
        adapter!!.notifyDataSetChanged()
    }

    // 메뉴버튼 누르면 navigation Drawer 나오게 하는 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // navigation Drawer에서 각 아이템이 클릭되었을 때 할일
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // 메인화면
            R.id.action_home -> {
                startToast("메인화면입니다.")
                drawerLayout.closeDrawers()
            }
            // 캘린더
            R.id.action_cal -> {
                myStartActivity(Cal::class.java)
            }
            // 회원정보
            R.id.action_account -> {
                myStartActivity(UserInfoActivity::class.java)
            }
            // 만보기
            R.id.action_walk -> {
                myStartActivity(StepActivity::class.java)
            }
            // 통계
            R.id.action_statics -> {
                myStartActivity(StatisticsActivity::class.java)
            }
            // 타이머
            R.id.action_timer -> {
                myStartActivity(Timer::class.java)
            }
            // 로그아웃
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut() // 사용자 로그아웃 시키는 signOut() (파이어베이스 참조)
                myStartActivity(SignUpActivity::class.java)
                startToast("로그아웃 되었습니다.")
            }
            // 앱 사용 법
            R.id.action_manual -> {
                myStartActivity(AppManual::class.java)
            }
            // 앱 정보
            R.id.action_information -> {
                myStartActivity(AppInformation::class.java)
            }
        }
        return false
    }

    // navigation Drawer가 열렸을 때 뒤로 가기 버튼을 누르면 navigation Drawer가 닫히게 하기
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
            moveTaskToBack(true)
            Process.killProcess(Process.myPid())
            exitProcess(1)
        }
    }

    // 토스트 메시지
    private fun startToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    // 인텐트 이동 함수
    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        startActivity(intent)
    }

    // SharedPreference로 키, 몸무게 저장
    private fun saveData(height: Int, weight: Int){
        var pref = this.getPreferences(0)
        var editor = pref.edit()
        editor.putInt("KEY_HEIGHT", heightEditText.text.toString().toInt()).apply()
        editor.putInt("KEY_WEIGHT", weightEditText.text.toString().toInt()).apply()
    }

    // SharedPreference에서 키, 몸무게 불러오기
    private fun loadData(){
        var pref=this.getPreferences(0)
        var height = pref.getInt("KEY_HEIGHT", 0)
        var weight = pref.getInt("KEY_WEIGHT", 0)
        if(height != 0 && weight != 0){
            heightEditText.setText(height.toString())
            weightEditText.setText(weight.toString())
        }
    }
}