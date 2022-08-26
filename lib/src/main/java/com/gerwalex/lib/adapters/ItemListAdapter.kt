package com.gerwalex.lib.adapters

import android.annotation.SuppressLint
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class ItemListAdapter<T> @JvmOverloads constructor(private val myList: MutableList<T> = ArrayList()) :
    ItemListAdapterTemplate<T>() {

    fun add(index: Int, item: T) {
        myList.add(index, item)
        notifyItemInserted(index)
    }

    override fun add(item: T) {
        add(myList.size - 1, item)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun addAll(items: List<T>) {
        myList.addAll(items)
        notifyDataSetChanged()
    }

    override fun addAll(items: Array<T>) {
        myList.addAll(listOf(*items))
    }

    override fun getID(item: T): Long {
        return RecyclerView.NO_ID
    }

    override fun getItemAt(position: Int): T {
        return myList[position]
    }

    override fun getItemCount(): Int {
        return myList.size
    }

    override fun getItemId(position: Int): Long {
        return getID(getItemAt(position))
    }

    val itemList: List<T>
        get() = myList
    val itemListSize: Int
        get() = myList.size

    override fun getPosition(item: T): Int {
        return myList.indexOf(item)
    }

    abstract override fun onBindViewHolder(holder: ViewHolder, item: T, position: Int)
    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(myList, fromPosition, toPosition)
        notifyItemChanged(fromPosition)
        notifyItemChanged(toPosition)
    }

    @SuppressLint("NotifyDataSetChanged")
    @CallSuper
    override fun replace(items: List<T>) {
        myList.clear()
        myList.addAll(items)
        notifyDataSetChanged()
    }

    @CallSuper
    override fun replaceItemAt(position: Int, item: T) {
        myList.removeAt(position)
        myList.add(position, item)
        notifyItemChanged(position)
    }

    fun remove(index: Int): T {
        return myList.removeAt(index)
    }
}
