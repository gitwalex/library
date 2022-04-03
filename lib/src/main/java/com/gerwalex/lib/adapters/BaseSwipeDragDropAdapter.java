package com.gerwalex.lib.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Basis-ExcludedCatsAdapter fuer RecyclerView. Unterstuetzt Swipe und Drag.
 */
public abstract class BaseSwipeDragDropAdapter extends RecyclerView.Adapter<ViewHolder> {
    private int dragFlags;
    private OnMoveListener mOnMoveListener;
    private OnSwipeListener mOnSwipeListener;
    private RecyclerView mRecyclerView;
    private ItemTouchHelper mTouchHelper;
    private ItemTouchHelper.Callback mTouchHelperCallback;
    private int swipeFlags;

    protected boolean canDropOver(RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
        return true;
    }

    public final Context getContext() {
        return mRecyclerView.getContext();
    }

    public abstract int getItemViewType(int position);

    /**
     * Liefert die Position der View in der Recyclerview.
     */
    protected final int getPosition(View childView) {
        return mRecyclerView.getChildAdapterPosition(childView);
    }

    public final RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @CallSuper
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        if (mTouchHelper != null) {
            mTouchHelper.attachToRecyclerView(mRecyclerView);
        }
    }

    /**
     * Liefert einen ViewHolder
     *
     * @param viewGroup ViewGroup, zu der diese View gehören wird
     * @param itemType  ItemType
     * @return ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemType) {
        return new ViewHolder(viewGroup, itemType);
    }

    /**
     * @param onMoveListener {@link OnMoveListener}
     */
    public void setDragable(OnMoveListener onMoveListener) {
        dragFlags = ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END;
        setDragable(onMoveListener, dragFlags);
    }

    /**
     * @param onMoveListener {@link OnMoveListener}
     * @param dragFlags      dragFlags siehe {@link ItemTouchHelper}
     */
    public void setDragable(OnMoveListener onMoveListener, int dragFlags) {
        mOnMoveListener = onMoveListener;
        this.dragFlags = dragFlags;
        mTouchHelperCallback = new SimpleItemTouchHelperCallback();
        mTouchHelper = new ItemTouchHelper(mTouchHelperCallback);
        if (mRecyclerView != null) {
            mTouchHelper.attachToRecyclerView(mRecyclerView);
        }
    }

    /**
     * @param onSwipeListener OnSwipeListener
     */
    public final void setSwipeable(OnSwipeListener onSwipeListener) {
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        setSwipeable(onSwipeListener, swipeFlags);
    }

    /**
     * @param OnSwipeListener OnSwipeListener
     * @param swipeFlags      Flags für Swipe (@see ItemTouchHelper)
     */
    public final void setSwipeable(OnSwipeListener OnSwipeListener, int swipeFlags) {
        mOnSwipeListener = OnSwipeListener;
        this.swipeFlags = swipeFlags;
        mTouchHelperCallback = new SimpleItemTouchHelperCallback();
        mTouchHelper = new ItemTouchHelper(mTouchHelperCallback);
        if (mRecyclerView != null) {
            mTouchHelper.attachToRecyclerView(mRecyclerView);
        }
    }

    public interface OnMoveListener {
        /**
         * @see SimpleItemTouchHelperCallback#clearView(RecyclerView, RecyclerView.ViewHolder)
         */
        void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder);

        /**
         * @see SimpleItemTouchHelperCallback#onMove(RecyclerView, RecyclerView.ViewHolder,
         * RecyclerView.ViewHolder)
         */
        boolean onMove(int fromPosition, int toPosition);

        /**
         * @see SimpleItemTouchHelperCallback#onSelectedChanged(RecyclerView.ViewHolder, int)
         */
        void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState);
    }

    public interface OnSwipeListener {
        void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }

    public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
        @Override
        public boolean canDropOver(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder current,
                                   @NonNull RecyclerView.ViewHolder target) {
            return BaseSwipeDragDropAdapter.this.canDropOver(current, target);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            if (mOnMoveListener != null) {
                mOnMoveListener.clearView(recyclerView, viewHolder);
            }
            super.clearView(recyclerView, viewHolder);
        }

        /**
         * Setzt die MovementFlags. In der Default-Implementation wird Dragging in alle Richtungen
         * unterstuetzt, ausserdem Swipe bei links oder rechts.
         */
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            // Set movement flags based on the layout manager
            int dragFlags = mOnMoveListener != null ? BaseSwipeDragDropAdapter.this.dragFlags : 0;
            int swipeFlags = mOnSwipeListener != null ? BaseSwipeDragDropAdapter.this.swipeFlags : 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            boolean moved = false;
            if (mOnMoveListener != null) {
                int from = viewHolder.getBindingAdapterPosition();
                int to = target.getBindingAdapterPosition();
                moved = mOnMoveListener.onMove(from, to);
                if (moved) {
                    notifyItemMoved(from, to);
                }
            }
            return moved;
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            Log.d("gerwalex", "onSelectedChanged " + actionState);
            if (mOnMoveListener != null) {
                mOnMoveListener.onSelectedChanged(viewHolder, actionState);
            }
        }

        @Override
        public final void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (mOnSwipeListener != null) {
                int position = viewHolder.getBindingAdapterPosition();
                mOnSwipeListener.onSwipe(viewHolder, direction, position);
            }
        }
    }
}

