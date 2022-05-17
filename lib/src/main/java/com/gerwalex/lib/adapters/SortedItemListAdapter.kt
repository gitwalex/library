package com.gerwalex.lib.adapters

import androidx.recyclerview.widget.SortedList

/**
 * ExcludedCatsAdapter mit einer [SortedList]. Dieser ExcludedCatsAdapter ist Swipeable, aber
 * nicht Dragable
 */
abstract class SortedItemListAdapter<T>(clazz: Class<T>) : ItemListAdapterTemplate<T>() {

    private val sortedItemList: SortedList<T>

    /**
     * @see SortedList.add
     */
    override fun add(item: T) {
        sortedItemList.add(item)
    }

    override fun addAll(items: List<T>) {
        sortedItemList.beginBatchedUpdates()
        for (item in items) {
            add(item)
        }
        sortedItemList.endBatchedUpdates()
    }

    override fun addAll(items: Array<T>) {
        addAll(listOf(*items))
    }

    /**
     * Wird aus dem ExcludedCatsAdapter gerufen, wenn [SortedItemListAdapter.areItemsTheSame] true zuruckgegeben hat. Dann kann hier angegeben werden, ob nicht nur die
     * Suchkritieren identisch sind, sindern auch der Inhalt.
     *
     * @param other das zu vergleichende Item
     * @return true, wenn die Inhalte gleich sind.
     */
    protected abstract fun areContentsTheSame(item: T, other: T): Boolean

    /**
     * Wird aus dem ExcludedCatsAdapter gerufen, wenn [SortedItemListAdapter.compare] '0' zuruckgegeben hat. Dann kann hier angegeben werden, ob die Suchkritieren
     * identisch sind.
     *
     * @param other das zu vergleichende Item
     * @return true, wenn die Suchkriterien gleich sind.
     */
    protected abstract fun areItemsTheSame(item: T, other: T): Boolean

    /**
     * Wird aus dem ExcludedCatsAdapter gerufen, um die Reihenfolge festzulegen.
     *
     * @param other das zu vergleichende Item
     * @return -1, wenn dieses Item vor other liegen soll 1, wenn dieses Item hinter other liegen
     * soll sonst 0. Dann wird [SortedItemListAdapter.areItemsTheSame]
     * gerufen
     */
    protected abstract fun compare(item: T, other: T): Int
    override fun getItemAt(position: Int): T {
        return sortedItemList[position]
    }

    override fun getItemCount(): Int {
        return sortedItemList.size()
    }

    override fun getItemId(position: Int): Long {
        return getID(sortedItemList[position])
    }

    override fun getPosition(item: T): Int {
        return sortedItemList.indexOf(item)
    }

    /**
     * Wird nicht unterstuetzt
     */
    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        throw UnsupportedOperationException("Drag wird von einer SortedList nicht unterstuetzt!")
    }

    /**
     * Sortiert die Liste neu. Nur sinnvoll, wenn sich das Sortierkriterium geaendert hat.
     */
    override fun recalculatePositions() {
        sortedItemList.beginBatchedUpdates()
        for (index in 0 until sortedItemList.size()) {
            sortedItemList.recalculatePositionOfItemAt(index)
        }
        sortedItemList.endBatchedUpdates()
    }

    override fun replace(items: List<T>) {
        reset()
        addAll(items)
    }

    override fun replaceItemAt(position: Int, item: T) {
        sortedItemList.updateItemAt(position, item)
    }

    override fun reset() {
        sortedItemList.clear()
    }

    private inner class MCallback : SortedList.Callback<T>() {

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return this@SortedItemListAdapter.areContentsTheSame(oldItem, newItem)
        }

        override fun areItemsTheSame(item1: T, item2: T): Boolean {
            return this@SortedItemListAdapter.areItemsTheSame(item1, item2)
        }

        override fun compare(o1: T, o2: T): Int {
            return this@SortedItemListAdapter.compare(o1, o2)
        }

        override fun onChanged(position: Int, count: Int) {
            notifyItemRangeChanged(position, count)
        }

        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }
    }

    init {
        sortedItemList = SortedList(clazz, MCallback())
    }
}