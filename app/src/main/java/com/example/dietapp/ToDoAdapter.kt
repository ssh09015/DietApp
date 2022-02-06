package com.example.dietapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView


class ToDoAdapter(private val myDB: DataBaseHelper, activity: MainActivity?) : RecyclerView.Adapter<ToDoAdapter.MyViewHolder>() {
    // 투두리스트 삭제, 수정, 추가 및 모든 기능 넣은 곳
    lateinit var mList: MutableList<ToDoModel>
    private val activity: MainActivity? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.activity_task_layout, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = mList[position]
        holder.todoCheckbox.text = item.task
        holder.todoCheckbox.isChecked = toBoolean(item.status)
        holder.todoCheckbox.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                myDB.updateStatus(item.id, 1)
            } else myDB.updateStatus(item.id, 0)
        }
    }

    fun toBoolean(num: Int): Boolean {
        return num != 0
    }

    val context: Context?
        get() = activity

    //투두 리스트 보이도록
    fun setTasks(mList: MutableList<ToDoModel>?) {
        this.mList = mList!!
        notifyDataSetChanged()
    }
    // 투두 리스트 삭제 함수
    fun deleteTask(position: Int) {
        val item = mList[position]
        myDB.deleteTask(item.id)
        mList.removeAt(position)
        notifyItemRemoved(position)

    }

    // 투두리스트 편집 함수
    //fun editItem(position: Int) {
        //val item = mList[position]
        //val bundle = Bundle()
       // bundle.putInt("id", item.id)
        //bundle.putString("task", item.task)
        //val task = AddNewTask()
        //task.arguments = bundle
        //task.show(activity!!.supportFragmentManager, task.tag)
    //}

    override fun getItemCount(): Int {
        return mList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var todoCheckbox: CheckBox

        init {
            todoCheckbox = itemView.findViewById(R.id.todoCheckbox)
        }
    }
}
