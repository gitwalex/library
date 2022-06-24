package com.gerwalex.lib.database

import android.database.Cursor
import androidx.room.RoomDatabase

open class LiveCursorNew(
    db: RoomDatabase,
    table: String,
    invalidationTracker: InvalidationListener<Cursor>,
) :
    LiveTableNew<Cursor>
        (db, table, invalidationTracker)