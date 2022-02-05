package com.example.dietapp

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_statistics.*

class StatisticsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var linelist: ArrayList<Entry>
    lateinit var lineDataSet: LineDataSet
    lateinit var lineData: LineData

    lateinit var edtDate: EditText
    lateinit var edtWeight: EditText
    lateinit var edtDateResult: EditText
    lateinit var edtWeightResult: EditText
    lateinit var btnInit: Button
    lateinit var btnInsert: Button
    lateinit var btnSelect: Button
    lateinit var btnUpdate: Button
    lateinit var btnDelete: Button
    lateinit var btnshow: Button

    lateinit var myHelper: myDBHelper
    lateinit var sqlDB: SQLiteDatabase

    lateinit var navigationView : NavigationView
    lateinit var drawerLayout : DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        setContentView(R.layout.navi_statistics)


        edtDate = findViewById(R.id.edtDate)
        edtWeight = findViewById(R.id.edtWeight)
        edtDateResult = findViewById(R.id.edtDateResult)
        edtWeightResult = findViewById(R.id.edtWeightResult)
        btnInit = findViewById(R.id.btnInit)
        btnInsert = findViewById(R.id.btnInsert)
        btnSelect = findViewById(R.id.btnSelect)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)
        btnshow = findViewById(R.id.btnshow)

        drawerLayout = findViewById(R.id.drawerLayoutStatistics)
        navigationView = findViewById(R.id.naviViewStatistics)

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
        var navi_header=navigationView.getHeaderView(0)
        var navigationnameTextView=navi_header.findViewById<NavigationView>(R.id.navigationnameTextView) as TextView // TextView로 바꾸기
        var navigationemailTextView=navi_header.findViewById<NavigationView>(R.id.navigationemailTextView) as TextView // TextView로 바꾸기

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
                        navigationnameTextView.text = document.data?.get("name").toString() // 불러온 사용자 이름으로 텍스트뷰 바꾸기
                        navigationemailTextView.setText(user.email); // 사용자 이메일 불러오기
                    } else {
                        Log.d(MainActivity.TAG, "No such document")
                    }
                }
            } else {
                Log.d(MainActivity.TAG, "get failed with ", task.exception)
            }
        }

        myHelper = myDBHelper(this)

        btnshow.setOnClickListener {
            Toast.makeText(this,"그래프를 한 번 터치해주세요!", Toast.LENGTH_LONG).show()

            var firstInput:String
            var secondInput:String
            var thirdInput:String
            var fourthInput:String
            var fifthInput:String
            var sixthInput:String
            var seventhInput:String



            sqlDB = myHelper.readableDatabase

            var cursor: Cursor

            cursor = sqlDB.rawQuery("SELECT * FROM staticsTBL;", null)

            var arr1=Array<String>(7, { "0f" })
            var arr2=Array<String>(7, { "0" })
            val xLabel: ArrayList<String> = ArrayList()

            var index:Int=0
            var count:Int=cursor.count
            cursor.moveToPosition(count)
            while (cursor.moveToPrevious()) {
                arr1[index] = cursor.getString(1) // 몸무게
                arr2[index] =cursor.getString(0) // 날짜
                if(index==6){
                    break
                }
                index++
            }
            xLabel.add(arr2[6])
            xLabel.add(arr2[5])
            xLabel.add(arr2[4])
            xLabel.add(arr2[3])
            xLabel.add(arr2[2])
            xLabel.add(arr2[1])
            xLabel.add(arr2[0])

            val xAxis: XAxis = line_chart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.valueFormatter = IndexAxisValueFormatter(xLabel)
            line_chart.xAxis.labelRotationAngle = -40F;

            firstInput=arr1[6]
            secondInput=arr1[5]
            thirdInput=arr1[4]
            fourthInput=arr1[3]
            fifthInput=arr1[2]
            sixthInput=arr1[1]
            seventhInput=arr1[0]


            linelist= ArrayList()

            linelist.add(Entry(0f, firstInput.toFloat()))
            linelist.add(Entry(1f, secondInput.toFloat()))
            linelist.add(Entry(2f, thirdInput.toFloat()))
            linelist.add(Entry(3f, fourthInput.toFloat()))
            linelist.add(Entry(4f, fifthInput.toFloat()))
            linelist.add(Entry(5f, sixthInput.toFloat()))
            linelist.add(Entry(6f, seventhInput.toFloat()))



            lineDataSet= LineDataSet(linelist, "kg")
            lineData=LineData(lineDataSet)
            line_chart.data=lineData
            lineDataSet.color= Color.BLACK

            //lineDataSet.setColors(*ColorTemplate.JOYFUL_COLORS)

            lineDataSet.valueTextColor= Color.BLACK
            lineDataSet.valueTextSize=10f
            //lineDataSet.setDrawFilled(true)

            line_chart.description.isEnabled = false

            cursor.close()
            sqlDB.close()
        }


        btnInit.setOnClickListener {
            sqlDB = myHelper.writableDatabase
            myHelper.onUpgrade(sqlDB, 1, 2)
            sqlDB.close()
        }

        btnInsert.setOnClickListener {
            sqlDB = myHelper.writableDatabase

            sqlDB.execSQL(
                "INSERT INTO staticsTBL VALUES ('" + edtDate.text.toString() + "',"
                        + edtWeight.text.toString() + ");"
            )

            sqlDB.close()
            Toast.makeText(applicationContext, "입력됨", Toast.LENGTH_SHORT).show()
            btnSelect.callOnClick()
        }

        btnSelect.setOnClickListener {
            sqlDB = myHelper.readableDatabase

            var cursor: Cursor
            cursor = sqlDB.rawQuery("SELECT * FROM staticsTBL;", null)

            var strDate = "날짜" + "\r\n" + "---------" + "\r\n"
            var strWeight = "몸무게" + "\r\n" + "---------" + "\r\n"

            var count:Int=cursor.count
            cursor.moveToPosition(count)
            while (cursor.moveToPrevious()) {
                strDate += cursor.getString(0) + "\r\n"
                strWeight += cursor.getString(1) + "\r\n"
            }

            edtDateResult.setText(strDate)
            edtWeightResult.setText(strWeight)

            cursor.close()
            sqlDB.close()
        }

        btnUpdate.setOnClickListener {
            sqlDB = myHelper.writableDatabase

            sqlDB.execSQL(
                "UPDATE staticsTBL SET gWeight = " + edtWeight.text + " WHERE gDate = '"
                        + edtDate.text.toString() + "';"
            )

            sqlDB.close()

            Toast.makeText(applicationContext, "수정됨", Toast.LENGTH_SHORT).show()
            btnSelect.callOnClick()
        }

        btnDelete.setOnClickListener {
            sqlDB = myHelper.writableDatabase

            sqlDB.execSQL("DELETE FROM staticsTBL WHERE gDate = '" + edtDate.text.toString() + "';")


            sqlDB.close()
            Toast.makeText(applicationContext, "삭제됨", Toast.LENGTH_SHORT).show()
            btnSelect.callOnClick()
        }
    }
    inner class myDBHelper(context: Context):SQLiteOpenHelper(context, "statics", null, 1){
        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL("CREATE TABLE staticsTBL (gDate CHAR(20) PRIMARY KEY, gWeight FLOAT);")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS staticsTBL")
            onCreate(db)
        }
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
                myStartActivity(MainActivity::class.java)
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
                Toast.makeText(this, "여기가 통계 화면입니다.", Toast.LENGTH_SHORT).show()
                drawerLayout.closeDrawers()
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
            R.id.action_manual -> {
                //앱 사용 방법 화면으로 이동
                myStartActivity(AppManual::class.java)
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
            myStartActivity(MainActivity::class.java)
        }
    }

    private fun myStartActivity(c: Class<*>) { // 인텐트 이동을 따로 함수로 만듦 (세이)
        val intent = Intent(this, c)
        startActivity(intent)
    }
}

