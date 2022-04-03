package com.gerwalex.lib.adapters;

import static androidx.recyclerview.widget.RecyclerView.NO_ID;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Template eines AdapterType mit Liste.
 */
public abstract class ItemListAdapterTemplate<T> extends BaseSwipeDragDropAdapter {
    /**
     * Fuegt ein Item der Liste hinzu.
     */
    public abstract void add(@NonNull T item);

    /**
     * Fuegt alle Items einer Liste hinzu.
     *
     * @param items Liste mit Items.
     */
    public abstract void addAll(@NonNull List<T> items);

    /**
     * Fuegt alle Items zu einer Liste hinzu.
     *
     * @param items Array mit Items.
     */
    public abstract void addAll(@NonNull T[] items);

    /**
     * @return Liefert die ID zuruck.
     */
    protected long getID(@NonNull T item) {
        return NO_ID;
    }

    /**
     * @param position Position des Items
     * @return Liefert ein Item an der Position zuruck.
     */
    public abstract T getItemAt(int position);

    /**
     * @return die Anzahl der Items.
     */
    @Override
    public abstract int getItemCount();

    /**
     * @param position Position
     * @return Liefert das Item an position zuruck
     * @throws IndexOutOfBoundsException wenn size < position oder position < 0
     */
    @Override
    public abstract long getItemId(int position);

    /**
     * @param item Item
     * @return Liefert die Position des Items
     */
    public abstract int getPosition(@NonNull T item);

    /**
     * @param holder   ViewHolder
     * @param item     Item zum binden
     * @param position Position des Items
     */
    protected abstract void onBindViewHolder(ViewHolder holder, T item, int position);

    @Override
    public final void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        onBindViewHolder(holder, getItemAt(position), position);
    }

    /**
     * Wird gerufen, wenn ein Item die Position aendert
     *
     * @param fromPosition Urspruenglich Position des Items
     * @param toPosition   Neue Position des Items
     */
    public abstract void onItemMoved(int fromPosition, int toPosition);

    /**
     * Kalkuliert Positionen von Items neu. Die Default-Implementierung macht hier nichts.
     */
    public void recalculatePositions() {
    }

    /**
     * Tauscht die Liste aus
     *
     * @param items Liste mit Items
     */
    public abstract void replace(@NonNull List<T> items);

    /**
     * Tauscht die Liste aus
     *
     * @param items Array mit Items
     */
    public final void replace(T[] items) {
        if (items != null) {
            replace(Arrays.asList(items));
        } else {
            reset();
            replace(new ArrayList<>());
        }
    }

    /**
     * Tauscht das Item an der Stelle position aus.
     *
     * @param position Position
     * @param item     Item
     */
    public abstract void replaceItemAt(int position, @NonNull T item);

    /**
     * Setzt die Liste zurueck.
     */
    public void reset() {
    }
}
