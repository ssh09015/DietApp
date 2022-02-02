package com.example.dietapp

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

// 만보기(세이)
class StepActivity : AppCompatActivity(), SensorEventListener {
    var sensorManager: SensorManager? = null
    var stepCountSensor: Sensor? = null
    var stepCountView: TextView? = null
    var resetButton: Button? = null
    lateinit var ctoastbutton: Button

    // 현재 걸음 수
    var currentSteps = 0

    // 현재 칼로리수
    var calorie = 0
    @RequiresApi(api = Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step)
        stepCountView = findViewById(R.id.stepCountView)
        resetButton=findViewById(R.id.resetButton)
        ctoastbutton=findViewById(R.id.ctoastbutton)

        resetButton?.setOnClickListener(View.OnClickListener {
            currentSteps=0
            stepCountView?.text=currentSteps.toString()
        })

        // 활동 퍼미션 체크
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 0)
        }

        // 걸음 센서 연결
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCountSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        // 디바이스에 걸음 센서의 존재 여부 체크
        if (stepCountSensor == null) {
            Toast.makeText(this, "No Step Sensor", Toast.LENGTH_SHORT).show()
        }

        //칼로리 계산 토스트 버튼
        calorie = 33 * currentSteps
        ctoastbutton.setOnClickListener {
            Toast.makeText(this, "$calorie"+"Cal이 소모되었습니다.", Toast.LENGTH_LONG).show()
        }
    }

    public override fun onStart() {
        super.onStart()
        if (stepCountSensor != null) {
            sensorManager!!.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST) // 센서 속도 설정
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        // 걸음 센서 이벤트 발생시
        if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
            if (event.values[0] == 1.0f) {
                // 센서 이벤트가 발생할때 마다 걸음수 증가
                currentSteps++
                stepCountView!!.text = currentSteps.toString()
            }
        }
    }
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    //토스트 버튼 함수
    private fun startToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }




}