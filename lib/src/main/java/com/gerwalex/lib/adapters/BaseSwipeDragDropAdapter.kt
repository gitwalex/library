package com.gerwalex.lib.adapters

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * Basis-ExcludedCatsAdapter fuer RecyclerView. Unterstuetzt Swipe und Drag.
 */
abstract class BaseSwipeDragDropAdapter : RecyclerView.Adapter<ViewHolder>(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private var job = Job()
    private var dragFlags = 0
    private var mOnMoveListener: OnMoveListener? = null
    private var mOnSwipeListener: OnSwipeListener? = null
    var recyclerView: RecyclerView? = null
        private set
    private var mTouchHelper: ItemTouchHelper? = null
    private var mTouchHelperCallback: ItemTouchHelper.Callback? = null
    private var swipeFlags = 0
    protected open fun canDropOver(current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return true
    }

    val context: Context
        get() = recyclerView!!.context

    abstract override fun getItemViewType(position: Int): Int
    /**
     * Liefert die Position der View in der Recyclerview.
     */
    /**
     * Liefert die Position der View in der Recyclerview.
     */
    protected fun getPosition(childView: View): Int {
        return recyclerView!!.getChildAdapterPosition(childView)
    }

    @CallSuper
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        if (mTouchHelper != null) {
            mTouchHelper!!.attachToRecyclerView(this.recyclerView)
        }
    }

    /**
     * Liefert einen ViewHolder
     *
     * @param viewGroup ViewGroup, zu der diese View gehören wird
     * @param itemType  ItemType
     * @return ViewHolder
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, itemType: Int): ViewHolder {
        return ViewHolder(viewGroup, itemType)
    }

    /**
     * @param onMoveListener [OnMoveListener]
     */
    fun setDragable(onMoveListener: OnMoveListener) {
        dragFlags = ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.START or ItemTouchHelper.END
        setDragable(onMoveListener, dragFlags)
    }

    /**
     * @param onMoveListener [OnMoveListener]
     * @param dragFlags      dragFlags siehe [ItemTouchHelper]
     */
    fun setDragable(onMoveListener: OnMoveListener, dragFlags: Int) {
        mOnMoveListener = onMoveListener
        this.dragFlags = dragFlags
        val touchHelper = SimpleItemTouchHelperCallback()
        mTouchHelperCallback = touchHelper
        mTouchHelper = ItemTouchHelper(touchHelper)
        if (recyclerView != null) {
            mTouchHelper!!.attachToRecyclerView(recyclerView)
        }
    }

    /**
     * @param onSwipeListener OnSwipeListener
     */
    fun setSwipeable(onSwipeListener: OnSwipeListener?) {
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        setSwipeable(onSwipeListener, swipeFlags)
    }

    /**
     * @param OnSwipeListener OnSwipeListener
     * @param swipeFlags      Flags für Swipe (@see ItemTouchHelper)
     */
    fun setSwipeable(OnSwipeListener: OnSwipeListener?, swipeFlags: Int) {
        mOnSwipeListener = OnSwipeListener
        this.swipeFlags = swipeFlags
        val touchHelper = SimpleItemTouchHelperCallback()
        mTouchHelperCallback = touchHelper
        mTouchHelper = ItemTouchHelper(touchHelper)
        if (recyclerView != null) {
            mTouchHelper!!.attachToRecyclerView(recyclerView)
        }
    }

    interface OnMoveListener {
        /**
         * @see SimpleItemTouchHelperCallback.clearView
         */
        /**
         * @see SimpleItemTouchHelperCallback.clearView
         */
        fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder)
        /**
         * @see SimpleItemTouchHelperCallback.onMove
         */
        /**
         * @see SimpleItemTouchHelperCallback.onMove
         */
        fun onMove(fromPosition: Int, toPosition: Int): Boolean
        /**
         * @see SimpleItemTouchHelperCallback.onSelectedChanged
         */
        /**
         * @see SimpleItemTouchHelperCallback.onSelectedChanged
         */
        fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder, actionState: Int)
    }

    interface OnSwipeListener {

        fun onSwipe(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int)
    }

    inner class SimpleItemTouchHelperCallback : ItemTouchHelper.Callback() {

        override fun canDropOver(
            recyclerView: RecyclerView, current: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {
            return this@BaseSwipeDragDropAdapter.canDropOver(current, target)
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            mOnMoveListener?.clearView(recyclerView, viewHolder)
            super.clearView(recyclerView, viewHolder)
        }
        /**
         * Setzt die MovementFlags. In der Default-Implementation wird Dragging in alle Richtungen
         * unterstuetzt, ausserdem Swipe bei links oder rechts.
         */
        /**
         * Setzt die MovementFlags. In der Default-Implementation wird Dragging in alle Richtungen
         * unterstuetzt, ausserdem Swipe bei links oder rechts.
         */
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            // Set movement flags based on the layout manager
            val dragFlags = if (mOnMoveListener != null) dragFlags else 0
            val swipeFlags = if (mOnSwipeListener != null) swipeFlags else 0
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {
            var moved = false
            mOnMoveListener?.run {
                val from = viewHolder.bindingAdapterPosition
                val to = target.bindingAdapterPosition
                moved = onMove(from, to)
                if (moved) {
                    notifyItemMoved(from, to)
                }
            }
            return moved
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            Log.d("gerwalex", "onSelectedChanged $actionState")
            mOnMoveListener?.onSelectedChanged(viewHolder!!, actionState)
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (mOnSwipeListener != null) {
                val position = viewHolder.bindingAdapterPosition
                mOnSwipeListener!!.onSwipe(viewHolder, direction, position)
            }
        }
    }
}