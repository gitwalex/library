package com.gerwalex.lib.database;

import android.database.Cursor;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;

public abstract class LiveCursor extends LiveTable<Cursor> {
    public LiveCursor(@NonNull RoomDatabase db, @NonNull String table, String... moreTables) {
        super(db, table, moreTables);
    }

    /**
     * Wenn keine Observer mehr vorhanden sind wird der Cursor ggfs. geschlossen
     */
    @CallSuper
    @Override
    protected void onInactive() {
        if (!hasObservers()) {
            Cursor c = getValue();
            if (c != null && !c.isClosed()) {
                c.close();
                Log.d("gerwalex", "Cursor closed");
            }
        }
        super.onInactive();
    }
}
