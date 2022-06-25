package com.gerwalex.lib.database

import android.database.Cursor
import androidx.room.RoomDatabase

@Deprecated("LiveCursor is deprecated.", level = DeprecationLevel.WARNING)
abstract class LiveCursor(
    db: RoomDatabase,
    table: String,
    vararg moreTables: String?,
) :
    LiveTable<Cursor>
        (db, table, *moreTables)