package com.example.dietapp

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

abstract class SwipeGesture(context: Context) : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {

    // 투두리스트 항목 오른쪽을 스와이프 할 경우 나타나는 제스쳐

    val deleteColor = ContextCompat.getColor(context,R.color.deletecolor)
    val deleteIcon = R.drawable.ic_baseline_delete
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(deleteColor)
                .addSwipeLeftActionIcon(deleteIcon)
                .create()
                .decorate()
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

}