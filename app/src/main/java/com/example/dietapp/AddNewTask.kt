package com.example.dietapp

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.widget.EditText
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.text.TextWatcher
import android.text.Editable
import android.content.DialogInterface
import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.Button

class AddNewTask : BottomSheetDialogFragment() {

    // 투두리스트 추가버튼 누를때 나오는 kt
    lateinit var enterText: EditText
    lateinit var saveButton: Button
    lateinit var myDB: DataBaseHelper


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_add_newtask, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterText = view.findViewById(R.id.enterText)
        saveButton = view.findViewById(R.id.saveButton)
        myDB = DataBaseHelper(activity)

        //db 업데이트 (지인)
        var isUpdate = false
        val bundle = arguments
        if (bundle != null) {
            isUpdate = true
            val task = bundle.getString("task")
            enterText.setText(task)
            if (task!!.length > 0) {
                saveButton.setEnabled(false)
            }
        }
        //할일을 입력했을 경우 저장 버튼 색 변경

        enterText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString() == "") {
                    saveButton.setEnabled(false)
                    saveButton.setBackgroundColor(Color.GRAY)
                } else {
                    saveButton.setEnabled(true)
                    saveButton.setBackgroundColor(resources.getColor(R.color.teal_700))
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        val finalIsUpdate = isUpdate

        // 할일 입력 후 저장 버튼 눌렀을 경우 db에 저장되도록
        saveButton.setOnClickListener(View.OnClickListener {
            val text = enterText.getText().toString()
            if (finalIsUpdate) {
                myDB!!.updateTask(bundle!!.getInt("id"), text)
            } else {
                val item = ToDoModel()
                item.task = text
                item.status = 0
                myDB!!.insertTask(item)
            }
            dismiss()
        })
    }

    // 창 사라지도록 하는
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val activity: Activity? = activity
        if (activity is OnDialogCloseListner) {
            (activity as OnDialogCloseListner).onDialogClose(dialog)
        }
    }

    companion object {
        const val TAG = "AddNewTask"
        @kotlin.jvm.JvmStatic
        fun newInstance(): AddNewTask {
            return AddNewTask()
        }
    }
}
