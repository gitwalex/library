package com.gerwalex.lib.adapters;

import static androidx.recyclerview.widget.RecyclerView.NO_ID;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class ItemListAdapter<T> extends ItemListAdapterTemplate<T> {
    private final List<T> myList;

    public ItemListAdapter(List<T> data) {
        myList = data;
    }

    public ItemListAdapter() {
        this(new ArrayList<>());
    }

    @Override
    public void add(@NonNull T item) {
        myList.add(item);
        notifyItemInserted(myList.size() - 1);
    }

    @Override
    public void addAll(@NonNull List<T> items) {
        myList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public final void addAll(@NonNull T[] items) {
        myList.addAll(Arrays.asList(items));
    }

    @Override
    protected long getID(@NonNull T item) {
        return NO_ID;
    }

    @Override
    public T getItemAt(int position) {
        return myList.get(position);
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    @Override
    public long getItemId(int position) {
        return getID(getItemAt(position));
    }

    public final List<T> getItemList() {
        return myList;
    }

    public int getItemListSize() {
        return myList.size();
    }

    @Override
    public final int getPosition(@NonNull T item) {
        return myList.indexOf(item);
    }

    @Override
    protected abstract void onBindViewHolder(ViewHolder holder, T item, int position);

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        Collections.swap(myList, fromPosition, toPosition);
        notifyItemChanged(fromPosition);
        notifyItemChanged(toPosition);
    }

    @CallSuper
    @Override
    public void replace(@NonNull List<T> items) {
        myList.clear();
        myList.addAll(items);
        notifyDataSetChanged();
    }

    @CallSuper
    @Override
    public void replaceItemAt(int position, @NonNull T item) {
        myList.remove(position);
        myList.add(position, item);
        notifyItemChanged(position);
    }
}
