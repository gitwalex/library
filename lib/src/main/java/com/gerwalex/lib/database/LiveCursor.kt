package com.gerwalex.lib.database

import android.database.Cursor
import android.util.Log
import androidx.annotation.CallSuper
import androidx.room.RoomDatabase

abstract class LiveCursor(private val db: RoomDatabase, table: String, vararg moreTables: String?) :
    LiveTable<Cursor>
        (db, table, *moreTables) {


    /**
     * Wenn keine Observer mehr vorhanden sind wird der Cursor ggfs. geschlossen
     */
    @CallSuper
    override fun onInactive() {
        if (!hasObservers()) {
            val c: Cursor? = value
            if (c != null && !c.isClosed) {
                c.close()
                Log.d("gerwalex", "Cursor closed")
            }
        }
        super.onInactive()
    }

}