package com.gerwalex.lib.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.room.RoomDatabase;

import com.gerwalex.lib.main.App;

import java.util.Set;

public abstract class LiveTable<T> extends LiveTableObserver<T> {
    public final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public LiveTable(@NonNull RoomDatabase db, @NonNull String table, String... moreTables) {
        super(db, table, moreTables);
    }

    protected final void invalidate(Set<String> tables) {
        loading.postValue(true);
        App.run((() -> {
            synchronized (loading) {
                postValue(onInvalidated(tables));
                loading.postValue(false);
            }
        }));
    }

    /**
     * Lädt Daten.
     *
     * @param tables Namen der geänderten Tabellen. null, wenn direkt {@link #invalidate()}
     *               aufgerufen wurde.
     * @return T
     */
    protected abstract T onInvalidated(@Nullable Set<String> tables);
}
