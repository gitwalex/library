package com.gerwalex.lib.adapters

import androidx.recyclerview.widget.RecyclerView

/**
 * Template eines AdapterType mit Liste.
 */
abstract class ItemListAdapterTemplate<T> : BaseSwipeDragDropAdapter() {

    /**
     * Fuegt ein Item der Liste hinzu.
     */
    abstract fun add(item: T)

    /**
     * Fuegt alle Items einer Liste hinzu.
     *
     * @param items Liste mit Items.
     */
    abstract fun addAll(items: List<T>)

    /**
     * Fuegt alle Items zu einer Liste hinzu.
     *
     * @param items Array mit Items.
     */
    abstract fun addAll(items: Array<T>)

    /**
     * @return Liefert die ID zuruck.
     */
    protected open fun getID(item: T): Long {
        return RecyclerView.NO_ID
    }

    /**
     * @param position Position des Items
     * @return Liefert ein Item an der Position zuruck.
     */
    abstract fun getItemAt(position: Int): T

    /**
     * @return die Anzahl der Items.
     */
    abstract override fun getItemCount(): Int

    /**
     * @param position Position
     * @return Liefert das Item an position zuruck
     * @throws IndexOutOfBoundsException wenn size < position oder position < 0
     */
    abstract override fun getItemId(position: Int): Long

    /**
     * @param item Item
     * @return Liefert die Position des Items
     */
    abstract fun getPosition(item: T): Int

    /**
     * @param holder   ViewHolder
     * @param item     Item zum binden
     * @param position Position des Items
     */
    protected abstract fun onBindViewHolder(holder: ViewHolder, item: T, position: Int)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBindViewHolder(holder, getItemAt(position), position)
    }

    /**
     * Wird gerufen, wenn ein Item die Position aendert
     *
     * @param fromPosition Urspruenglich Position des Items
     * @param toPosition   Neue Position des Items
     */
    abstract fun onItemMoved(fromPosition: Int, toPosition: Int)

    /**
     * Kalkuliert Positionen von Items neu. Die Default-Implementierung macht hier nichts.
     */
    open fun recalculatePositions() {}

    /**
     * Tauscht die Liste aus
     *
     * @param items Liste mit Items
     */
    abstract fun replace(items: List<T>)

    /**
     * Tauscht die Liste aus
     *
     * @param items Array mit Items
     */
    fun replace(items: Array<T>?) {
        if (items != null) {
            replace(listOf(*items))
        } else {
            reset()
            replace(ArrayList())
        }
    }

    /**
     * Tauscht das Item an der Stelle position aus.
     *
     * @param position Position
     * @param item     Item
     */
    abstract fun replaceItemAt(position: Int, item: T)

    /**
     * Setzt die Liste zurueck.
     */
    open fun reset() {}
}