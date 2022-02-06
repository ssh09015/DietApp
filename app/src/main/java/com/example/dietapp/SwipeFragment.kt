package com.example.dietapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_bmi_result.*
import kotlinx.android.synthetic.main.activity_user_info.*


class SwipeFragment : Fragment() {
    private var image: Int? = null
    private var text: String? = null
    //외부에서 전달되는 image와 text 값을 newInstance에서초기화
    //onCreate에서 값을 부여
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            //image , text container에 넣음
            image = it.getInt("image",0)
            text = it.getString("text","")
        }
    }

    //화면 만들기
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_swipe_fragment, container,false)
    }

    //이미지뷰, 텍스트뷰 표시
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manualImageView.setImageResource(image!!)
        manualTextView.text = text
    }

    //fragment 처음 만들 때 newInstance 함수 실행
    companion object{
        //외부로부터 image와 text 파라미터 값 받아옴
        fun newInstance(image: Int, text: String) =
            SwipeFragment().apply {
                //keyvalue 형식으로 받음
                arguments = Bundle().apply {
                    putInt("image",image)
                    putString("text", text)
                }
            }
    }


}