package com.gerwalex.lib.database;

import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;

import com.gerwalex.lib.main.App;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class LiveTableObserver<T> extends MutableLiveData<T> {
    protected final Set<String> tables;
    private final RoomDatabase db;
    private final InvalidationTracker.Observer tracker;

    public LiveTableObserver(@NonNull RoomDatabase db, @NonNull String table, String... moreTables) {
        this.db = db;
        tracker = new InvalidationTracker.Observer(table, moreTables) {
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
                invalidate(tables);
                Log.d("gerwalex",
                        String.format("DatabaseTable invalidated (%1s) in %2s", tables, LiveTableObserver.this));
            }
        };
        tables = new HashSet<>(Arrays.asList(moreTables));
        tables.add(table);
    }

    public void invalidate() {
        invalidate(tables);
    }

    /**
     * Lädt Daten.
     *
     * @param tables Namen der geänderten Tabellen.
     */
    protected abstract void invalidate(@Nullable Set<String> tables);

    @Override
    public void observeForever(@NonNull Observer<? super T> observer) {
        throw new IllegalStateException(
                "observeForever() zur Vermeidung von MemoryLeaks nicht möglich. Datenbank erhält Referenz auf " +
                        getClass().getName());
    }

    @CallSuper
    @Override
    protected void onActive() {
        super.onActive();
        App.run((() -> {
            db.getInvalidationTracker().addObserver(tracker);
        }));
        invalidate(tables);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        App.run((() -> {
            db.getInvalidationTracker().removeObserver(tracker);
            Log.d("gerwalex", "LiveDataBaseTracker unregistered (Tables: " + tables);
        }));
    }
}
