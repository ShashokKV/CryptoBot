package com.chess.cryptobot.view.adapter

import androidx.recyclerview.widget.RecyclerView
import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.model.ViewItem

abstract class RecyclerViewAdapter<T : RecyclerView.ViewHolder?> internal constructor(holder: ContextHolder) : RecyclerView.Adapter<T>() {
    private val holder: ContextHolder
    fun itemNameByPosition(position: Int): String {
        return getItemByPosition(position).name
    }

    fun getItemByPosition(position: Int): ViewItem {
        return holder.viewItems[position]
    }

    fun notifyItemInserted() {
        this.notifyItemInserted(itemCount)
    }

    fun deleteItem(position: Int) {
        notifyItemRemoved(position)
    }

    fun updateItem(item: ViewItem?) {
        val index = holder.viewItems.indexOf(item)
        if (index >= 0) {
            this.notifyItemChanged(index)
        }
    }

    override fun getItemCount(): Int {
        return holder.viewItems.size
    }

    override fun getItemId(position: Int): Long {
        val viewItem = holder.viewItems[position]
        return viewItem.name.hashCode().toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    init {
        enableStableIds()
        this.holder = holder
    }

    private fun enableStableIds() {
        setHasStableIds(true)
    }
}