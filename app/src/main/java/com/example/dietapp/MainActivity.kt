package com.example.dietapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    lateinit var resultButton: Button//추후에 초기화 변수타입
    lateinit var heightEditText: EditText
    lateinit var weightEditText: EditText
    lateinit var recommandButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultButton = findViewById<Button>(R.id.resultButton)
        heightEditText = findViewById<EditText>(R.id.heightEditText)
        weightEditText = findViewById<EditText>(R.id.weightEditText)
        recommandButton = findViewById<Button>(R.id.recommendButton)

        loadData()

        resultButton.setOnClickListener {

            saveData(heightEditText.text.toString().toInt(), weightEditText.text.toString().toInt())
            //버튼이 눌릴때 동작
            var intent = Intent(this, ResultActivity::class.java) //bmi결과페이지로 이동
            intent.putExtra("height", heightEditText.text.toString()) //입력된 키값 가져오기
            intent.putExtra("weight", weightEditText.text.toString())
            startActivity(intent)
        }

        recommandButton.setOnClickListener { //추천 버튼 누르면 나오는 액티비티(세이)
            var intent=Intent(this, recommand::class.java)
            startActivity(intent)
        }
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



}