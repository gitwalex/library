package com.gerwalex.lib.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.SortedList;

import java.util.Arrays;
import java.util.List;

/**
 * ExcludedCatsAdapter mit einer {@link SortedList}. Dieser ExcludedCatsAdapter ist Swipeable, aber
 * nicht Dragable
 */
public abstract class SortedItemListAdapter<T> extends ItemListAdapterTemplate<T> {
    private final SortedList<T> sortedItemList;

    public SortedItemListAdapter(@NonNull Class<T> clazz) {
        sortedItemList = new SortedList<>(clazz, new MCallback());
    }

    /**
     * @see SortedList#add(T item)
     */
    @Override
    public final void add(@NonNull T item) {
        sortedItemList.add(item);
    }

    @Override
    public final void addAll(@NonNull List<T> items) {
        sortedItemList.beginBatchedUpdates();
        for (T item : items) {
            add(item);
        }
        sortedItemList.endBatchedUpdates();
    }

    @Override
    public final void addAll(@NonNull T[] items) {
        addAll(Arrays.asList(items));
    }

    /**
     * Wird aus dem ExcludedCatsAdapter gerufen, wenn {@link SortedItemListAdapter#areItemsTheSame(T
     * item, T other)} true zuruckgegeben hat. Dann kann hier angegeben werden, ob nicht nur die
     * Suchkritieren identisch sind, sindern auch der Inhalt.
     *
     * @param other das zu vergleichende Item
     * @return true, wenn die Inhalte gleich sind.
     */
    protected abstract boolean areContentsTheSame(T item, T other);

    /**
     * Wird aus dem ExcludedCatsAdapter gerufen, wenn {@link SortedItemListAdapter#compare(T item, T
     * other)} '0' zuruckgegeben hat. Dann kann hier angegeben werden, ob die Suchkritieren
     * identisch sind.
     *
     * @param other das zu vergleichende Item
     * @return true, wenn die Suchkriterien gleich sind.
     */
    protected abstract boolean areItemsTheSame(T item, T other);

    /**
     * Wird aus dem ExcludedCatsAdapter gerufen, um die Reihenfolge festzulegen.
     *
     * @param other das zu vergleichende Item
     * @return -1, wenn dieses Item vor other liegen soll 1, wenn dieses Item hinter other liegen
     * soll sonst 0. Dann wird {@link SortedItemListAdapter#areItemsTheSame(T iitem, T other)}
     * gerufen
     */
    protected abstract int compare(T item, T other);

    @Override
    public final T getItemAt(int position) {
        return sortedItemList.get(position);
    }

    @Override
    public final int getItemCount() {
        return sortedItemList.size();
    }

    @Override
    public final long getItemId(int position) {
        return getID(sortedItemList.get(position));
    }

    @Override
    public int getPosition(@NonNull T item) {
        return sortedItemList.indexOf(item);
    }

    /**
     * Wird nicht unterstuetzt
     */
    @Override
    public final void onItemMoved(int fromPosition, int toPosition) {
        throw new UnsupportedOperationException("Drag wird von einer SortedList nicht unterstuetzt!");
    }

    /**
     * Sortiert die Liste neu. Nur sinnvoll, wenn sich das Sortierkriterium geaendert hat.
     */
    @Override
    public void recalculatePositions() {
        sortedItemList.beginBatchedUpdates();
        for (int index = 0; index < sortedItemList.size(); index++) {
            sortedItemList.recalculatePositionOfItemAt(index);
        }
        sortedItemList.endBatchedUpdates();
    }

    @Override
    public final void replace(@NonNull List<T> items) {
        reset();
        addAll(items);
    }

    @Override
    public final void replaceItemAt(int position, @NonNull T item) {
        sortedItemList.updateItemAt(position, item);
    }

    @Override
    public final void reset() {
        sortedItemList.clear();
    }

    private class MCallback extends SortedList.Callback<T> {
        @Override
        public boolean areContentsTheSame(T oldItem, T newItem) {
            return SortedItemListAdapter.this.areContentsTheSame(oldItem, newItem);
        }

        @Override
        public boolean areItemsTheSame(T item1, T item2) {
            return SortedItemListAdapter.this.areItemsTheSame(item1, item2);
        }

        @Override
        public int compare(T o1, T o2) {
            return SortedItemListAdapter.this.compare(o1, o2);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }
    }
}
