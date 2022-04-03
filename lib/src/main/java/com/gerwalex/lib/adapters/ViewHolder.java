package com.gerwalex.lib.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

/**
 * ViewHolder fuer RecyclerView
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    private ViewDataBinding binding;

    /**
     * @param viewGroup viewGroup für Holderview
     * @param layout    resID des Layouts
     */
    public ViewHolder(@NonNull ViewGroup viewGroup, @LayoutRes int layout) {
        this(LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false));
    }

    /**
     * Erstellt ViewHolder.
     *
     * @param view View fuer den Holder
     */
    public ViewHolder(@NonNull View view) {
        super(view);
    }

    /**
     * Liefert Binding
     *
     * @throws IllegalArgumentException wenn view nicht bindable ist
     */
    @NonNull
    public ViewDataBinding getBinding() throws IllegalArgumentException {
        if (binding == null) {
            binding = Objects.requireNonNull(DataBindingUtil.bind(itemView));
        }
        return binding;
    }

    /**
     * @param vaiableID ID aus BR
     * @param item      Item für Binding
     * @throws IllegalArgumentException wenn view nicht bindable ist oder item nicht für Binding
     *                                  vorgesehen ist
     */
    public void setVariable(int vaiableID, Object item) throws IllegalArgumentException {
        if (!getBinding().setVariable(vaiableID, item)) {
            throw new IllegalArgumentException(
                    String.format("Objekt %1s nicht bekannt in Binding!", item.getClass().getSimpleName()));
        }
    }
}
