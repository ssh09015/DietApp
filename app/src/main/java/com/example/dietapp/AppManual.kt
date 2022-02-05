package com.example.dietapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.android.synthetic.main.activity_app_manual.*

class AppManual : AppCompatActivity() {

    private var vpAdapter: FragmentStateAdapter? = null

    //ViewPager와 어댑터 연결
    //indicator와 ViewPager 연결
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_manual)

        vpAdapter = CustomPagerAdapter(this)
        manualViewPager.adapter = vpAdapter

        indicator.setViewPager(manualViewPager)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)

        // 툴바를 액티비티의 앱바로 지정 (송하)
        setSupportActionBar(toolbar)

        // 드로어를 꺼낼 홈 버튼 활성화 (송하)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 홈버튼 (메뉴모양버튼으로) 이미지 변경 (송하)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        // 툴바에 타이틀 안보이게 (송하)
        supportActionBar?.setDisplayShowTitleEnabled(false)

    }



    class CustomPagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa){
        //전체 페이지수 7개
        private val PAGENUMBER = 7

        override fun getItemCount(): Int {
            return PAGENUMBER
        }

        //페이지별 반환할 것들 지정
        override fun createFragment(position: Int): Fragment {
            return when (position){
                0 -> SwipeFragment.newInstance(R.drawable.manual_join, "<회원가입>\n\n이메일과 비밀번호를 통해 \n회원가입을 진행할 수 있습니다.")
                1 -> SwipeFragment.newInstance(R.drawable.manual_login, "<로그인>\n\n이메일과 비밀번호를 입력해\n로그인할 수 있으며 " +
                        "비밀번호 재설정을\n 누를 경우, 이메일로 비밀번호 재설정\n 관련 메일이 보내집니다.")
                2 -> SwipeFragment.newInstance(R.drawable.manual_main, "<BMI 계산기>\n\n키와 몸무게를 입력하면 bmi 지수를 알려주며,\n" +
                        "상황에 맞게 추천 습관과 추천 운동을 알려줍니다.\n" +
                        "메인 화면의 하단에 위치한 추가 버튼을 통해 오늘 \n할 일들을 입력할 수 있습니다.\n")
                3 -> SwipeFragment.newInstance(R.drawable.manual_cal, "<캘린더>\n\n캘린더 메뉴를 통해 하루의 일어난 일들을 \n정리해볼 수 있습니다.\n" +
                        "매일 스스로 돌아보며 건강을 위한 \n습관을 발전시켜나갈 수 있습니다.\n")
                4 -> SwipeFragment.newInstance(R.drawable.manual_stepsensor, "<만보기>\n\n매일 걸을 때마다 만보기 기능을 이용하며,\n 걷는 습관을 활성화하길 권장합니다.\n" +
                        "칼로리 버튼을 누르면 걸음 수에 대한\n칼로리를 알려줍니다.\n")
                5 -> SwipeFragment.newInstance(R.drawable.manual_timer, "<타이머>\n\n앱 내 타이머를 통해 정해진 시간을 입력해두고 \n보다 정확한 운동을 진행하길 권장합니다.\n")
                6 -> SwipeFragment.newInstance(R.drawable.manual_graph, "<통계>\n\n날짜와 몸무게를 입력한 후 그래프를 누르면 \n몸무게 추이를 한 눈에 볼 수 있습니다.\n")
                else -> SwipeFragment.newInstance(R.drawable.manual_join,"<회원가입>\n\n이메일과 비밀번호를 통해 \n회원가입을 진행할 수 있습니다.")
            }
        }
    }
}
