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
    companion object {
        private const val TAG = "MainActivity"
    }
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

        // ????????? ??????????????? ????????? ??????
        setSupportActionBar(toolbar)
        // ???????????? ?????? ??? ?????? ?????????
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // ????????? (????????????????????????) ????????? ??????
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        // ????????? ????????? ????????????
        supportActionBar?.setDisplayShowTitleEnabled(false)
        // navigation ?????????
        navigationView.setNavigationItemSelectedListener(this)

        // navigation drawer header??? TextView??? ???????????????????????? ????????? ?????? ????????? ?????????
        var navi_header=navigationView.getHeaderView(0)
        var navigationnameTextView=navi_header.findViewById<NavigationView>(R.id.navigationnameTextView) as TextView
        var navigationemailTextView=navi_header.findViewById<NavigationView>(R.id.navigationemailTextView) as TextView

        // ????????????????????? ????????? ????????? ?????? ???????????? (?????????????????? ?????? ??????)
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        val docRef = user?.let { db.collection("users").document(it.uid) }
        docRef?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {
                    if (document.exists()) { // ????????? ?????????
                        Log.d(TAG, "DocumentSnapshot data: " + document.data)
                        // ????????? ????????? ???????????? navigation ???????????? ?????????
                        navigationnameTextView.text = document.data?.get("name").toString()
                        navigationemailTextView.setText(user.email); // ????????? ????????? ????????????
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
            } else {
                Log.d(TAG, "get failed with ", task.exception)
            }
        }
        myHelper = myDBHelper(this)

        // ????????? ?????? ?????? ???
        btnshow.setOnClickListener {
            startToast("???????????? ??? ??? ??????????????????!")
            var firstInput:String
            var secondInput:String
            var thirdInput:String
            var fourthInput:String
            var fifthInput:String
            var sixthInput:String
            var seventhInput:String

            // sqlite ????????????
            sqlDB = myHelper.readableDatabase
            var cursor: Cursor
            cursor = sqlDB.rawQuery("SELECT * FROM staticsTBL;", null)
            var arr1=Array<String>(7, { "0f" })
            var arr2=Array<String>(7, { "0" })
            val xLabel: ArrayList<String> = ArrayList()
            var index =0

            // ?????? ??????????????? ????????? ?????? ??????
            var count:Int=cursor.count
            cursor.moveToPosition(count)
            while (cursor.moveToPrevious()) {
                arr1[index] = cursor.getString(1) // ????????? ?????? ??????
                arr2[index] =cursor.getString(0) // ?????? ?????? ??????
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

            // x ??? ?????????
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

            // ???????????? ????????? ??? ??????
            linelist= ArrayList()
            linelist.add(Entry(0f, firstInput.toFloat()))
            linelist.add(Entry(1f, secondInput.toFloat()))
            linelist.add(Entry(2f, thirdInput.toFloat()))
            linelist.add(Entry(3f, fourthInput.toFloat()))
            linelist.add(Entry(4f, fifthInput.toFloat()))
            linelist.add(Entry(5f, sixthInput.toFloat()))
            linelist.add(Entry(6f, seventhInput.toFloat()))

            // ????????? ?????????
            lineDataSet= LineDataSet(linelist, "kg")
            lineData=LineData(lineDataSet)
            line_chart.data=lineData
            lineDataSet.color= Color.BLACK
            lineDataSet.valueTextColor= Color.BLACK
            lineDataSet.valueTextSize=10f
            line_chart.description.isEnabled = false
            cursor.close()
            sqlDB.close()
        }

        // ????????? ?????? ?????? ???
        btnInit.setOnClickListener {
            sqlDB = myHelper.writableDatabase
            myHelper.onUpgrade(sqlDB, 1, 2)
            sqlDB.close()
        }

        // ?????? ?????? ?????? ???
        btnInsert.setOnClickListener {
            sqlDB = myHelper.writableDatabase
            sqlDB.execSQL(
                "INSERT INTO staticsTBL VALUES ('" + edtDate.text.toString() + "',"
                        + edtWeight.text.toString() + ");"
            )
            sqlDB.close()
            startToast("?????????")
            btnSelect.callOnClick()
        }

        // ?????? ?????? ?????? ???
        btnSelect.setOnClickListener {
            sqlDB = myHelper.readableDatabase
            var cursor: Cursor
            cursor = sqlDB.rawQuery("SELECT * FROM staticsTBL;", null)
            var strDate = "??????" + "\r\n" + "---------" + "\r\n"
            var strWeight = "?????????" + "\r\n" + "---------" + "\r\n"

            // ?????? ??????????????? ????????? ?????? ??????
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

        // ?????? ?????? ?????? ???
        btnUpdate.setOnClickListener {
            sqlDB = myHelper.writableDatabase
            sqlDB.execSQL(
                "UPDATE staticsTBL SET gWeight = " + edtWeight.text + " WHERE gDate = '"
                        + edtDate.text.toString() + "';"
            )
            sqlDB.close()
            startToast("?????????")
            btnSelect.callOnClick()
        }

        // ?????? ?????? ?????? ???
        btnDelete.setOnClickListener {
            sqlDB = myHelper.writableDatabase
            sqlDB.execSQL("DELETE FROM staticsTBL WHERE gDate = '" + edtDate.text.toString() + "';")
            sqlDB.close()
            startToast("?????????")
            btnSelect.callOnClick()
        }
    }

    // ???????????? ????????? navigation Drawer ????????? ?????? ??????
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // navigation Drawer?????? ??? ???????????? ??????????????? ??? ??????
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // ?????? ??????
            R.id.action_home -> {
                myStartActivity(MainActivity::class.java)
            }
            // ?????????
            R.id.action_cal -> {
                myStartActivity(Cal::class.java)
            }
            // ?????? ??????
            R.id.action_account -> {
                myStartActivity(UserInfoActivity::class.java)
            }
            // ?????????
            R.id.action_walk -> {
                myStartActivity(StepActivity::class.java)
            }
            // ??????
            R.id.action_statics -> {
                startToast("????????? ?????? ???????????????.")
                drawerLayout.closeDrawers()
            }
            // ?????????
            R.id.action_timer -> {
                myStartActivity(Timer::class.java)
            }
            // ????????????
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                myStartActivity(SignUpActivity::class.java)
                startToast("???????????? ???????????????.")
            }
            // ??? ?????????
            R.id.action_manual -> {
                myStartActivity(AppManual::class.java)
            }
            // ?????????
            R.id.action_information -> {
                myStartActivity(AppInformation::class.java)
            }
        }
        return false
    }

    // sqlite ??????
    inner class myDBHelper(context: Context):SQLiteOpenHelper(context, "statics", null, 1){
        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL("CREATE TABLE staticsTBL (gDate CHAR(20) PRIMARY KEY, gWeight FLOAT);")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS staticsTBL")
            onCreate(db)
        }
    }

    // navigation Drawer??? ????????? ??? ?????? ?????? ????????? ????????? navigation Drawer??? ????????? ??????
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            myStartActivity(MainActivity::class.java)
        }
    }

    // ????????? ????????? ??????
    private fun startToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    // ????????? ?????? ??????
    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        startActivity(intent)
    }
}