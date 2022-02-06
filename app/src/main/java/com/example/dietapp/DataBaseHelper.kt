package com.example.dietapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*

class DataBaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    lateinit var db: SQLiteDatabase
    // 투두리스트 내부 db 이용해서 유지하기
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, TASK TEXT, STATUS INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun insertTask(model: ToDoModel) {
        db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_2, model.task)
        values.put(COL_3, 0)
        db.insert(TABLE_NAME, null, values)
    }

    fun updateTask(id: Int, task: String?) {
        db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_2, task)
        db.update(TABLE_NAME, values, "ID+?", arrayOf(id.toString()))
    }

    fun updateStatus(id: Int, status: Int) {
        db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_3, status)
        db.update(TABLE_NAME, values, "ID=?", arrayOf(id.toString()))
    }

    fun deleteTask(id: Int) {
        db = this.writableDatabase
        db.delete(TABLE_NAME, "ID=?", arrayOf(id.toString()))
    }

    @get:SuppressLint("Range")
    val allTask: MutableList<ToDoModel>
        get() {
            db = this.writableDatabase
            var cursor: Cursor? = null
            val modelList: MutableList<ToDoModel> = ArrayList()
            db.beginTransaction()
            try {
                cursor = db.query(TABLE_NAME, null, null, null, null, null, null)
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            val task = ToDoModel()
                            task.id = cursor.getInt(cursor.getColumnIndex(COL_1))
                            task.task = cursor.getString(cursor.getColumnIndex(COL_2))
                            task.status = cursor.getInt(cursor.getColumnIndex(COL_3))
                            modelList.add(task)
                        } while (cursor.moveToNext())
                    }
                }
            } finally {
                db.endTransaction()
                cursor!!.close()
            }
            return modelList
        }

    // db에 사용할 변수
    companion object {
        private const val DATABASE_NAME = "TODO_DATABASE"
        private const val TABLE_NAME = "TODO_TABLE"
        private const val COL_1 = "ID"
        private const val COL_2 = "TASK"
        private const val COL_3 = "STATUS"
    }
}
