package com.gerwalex.lib.adapters;

import static androidx.recyclerview.widget.RecyclerView.NO_ID;
import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

import android.database.Cursor;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

/**
 * Adapter fuer RecyclerView mit Cursor.
 */
public abstract class CursorAdapter extends BaseSwipeDragDropAdapter {
    protected Cursor mCursor;
    private int mRowIdColumnIndex = NO_POSITION;

    /**
     *
     */
    public CursorAdapter() {
        setHasStableIds(true);
    }

    protected Cursor getCursor() {
        return mCursor;
    }

    /**
     * @return Anzahl der im ExcludedCatsAdapter vorhandenen Items abzueglich der bereits entfernten
     * Items
     */
    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    /**
     * @param view ChildView der RecyclerView
     * @return ID des Items an dieser Psoition oder NO_ID
     */
    public final long getItemId(View view) {
        int position = getRecyclerView().getChildAdapterPosition(view);
        return position != NO_POSITION ? getItemId(position) : NO_ID;
    }

    /**
     * @param position Position in der RecyclerView
     * @return ID des Items an dieser Psoition
     */
    @Override
    public final long getItemId(int position) {
        return mRowIdColumnIndex != NO_POSITION && mCursor.moveToPosition(position) ?
                mCursor.getLong(mRowIdColumnIndex) : NO_ID;
    }

    @Override
    public final void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        onBindViewHolder(holder, mCursor, position);
    }

    protected abstract void onBindViewHolder(ViewHolder holder, Cursor mCursor, int position);

    /**
     * Swap in a new Cursor. The old Cursor is <em>not</em> closed.
     *
     * @param c new Cursor
     */
    @CallSuper
    public void swap(Cursor c) {
        mCursor = c;
        if (c != null) {
            mRowIdColumnIndex = c.getColumnIndex("_id");
        }
        notifyDataSetChanged();
    }
}