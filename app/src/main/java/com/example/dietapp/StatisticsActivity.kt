package com.example.dietapp

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.activity_statistics.*


class StatisticsActivity : AppCompatActivity() {

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)


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

        myHelper = myDBHelper(this)

        btnshow.setOnClickListener {
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
            var strWeight = "체중" + "\r\n" + "---------" + "\r\n"

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
}

