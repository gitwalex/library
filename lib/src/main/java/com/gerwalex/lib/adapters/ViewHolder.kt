package com.gerwalex.lib.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * ViewHolder fuer RecyclerView
 */
class ViewHolder
/**
 * Erstellt ViewHolder.
 *
 * @param view View fuer den Holder
 */
    (view: View) : RecyclerView.ViewHolder(view) {

    /**
     * Liefert Binding
     *
     * @throws IllegalArgumentException wenn view nicht bindable ist
     */
    val binding: ViewDataBinding = DataBindingUtil.bind(view)!!

    /**
     * @param viewGroup viewGroup für Holderview
     * @param layout    resID des Layouts
     */
    constructor(viewGroup: ViewGroup, @LayoutRes layout: Int) : this(LayoutInflater
        .from(viewGroup.context)
        .inflate(layout, viewGroup, false))

    /**
     * @param vaiableID ID aus BR
     * @param item      Item für Binding
     * @throws IllegalArgumentException wenn view nicht bindable ist oder item nicht für Binding
     * vorgesehen ist
     */
    @Throws(IllegalArgumentException::class)
    fun setVariable(vaiableID: Int, item: Any) {
        require(binding.setVariable(vaiableID, item)) {
            String.format("Objekt %1s nicht bekannt in Binding!",
                item.javaClass.simpleName)
        }
    }
}