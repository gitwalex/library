package com.gerwalex.lib.adapters

import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class ItemListAdapter<T> @JvmOverloads constructor(private val myList: MutableList<T> = ArrayList()) :
    ItemListAdapterTemplate<T>() {

    override fun add(item: T) {
        myList.add(item)
        notifyItemInserted(myList.size - 1)
    }

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
}