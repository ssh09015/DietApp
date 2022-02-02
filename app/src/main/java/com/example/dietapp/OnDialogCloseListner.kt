package com.example.dietapp

import android.content.DialogInterface

interface OnDialogCloseListner {
    // 추가 버튼 누르면 dialog.xml 나오도록 (지인)
    fun onDialogClose(dialogInterface: DialogInterface?)
}
