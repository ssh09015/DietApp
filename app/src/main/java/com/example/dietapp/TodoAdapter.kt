package com.example.dietapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//activity_list_item.xml로 구성한 투두 리스트를 activity_main.xml로 연결시켜주는 adapter (지인)

class TodoAdapter(val itemlist: ArrayList<Todolist>) : RecyclerView.Adapter<TodoAdapter.CustomViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_list_item, parent, false)
        return CustomViewHolder(view)
    }
    override fun getItemCount(): Int {
        return itemlist.size
    }

    override fun onBindViewHolder(holder: TodoAdapter.CustomViewHolder, position: Int) {
        holder.content.text = itemlist.get(position).content

    }



    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        //val checked = itemView.findViewById<CheckBox>(R.id.iv_checkbox)
        val content = itemView.findViewById<TextView>(R.id.tv_content)
    }

}
