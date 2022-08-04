package com.gerwalex.lib.database

import android.util.Log
import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

open class LiveTableNew<T>(
    private val db: RoomDatabase, private val table: String,
    private val invalidationTracker: InvalidationListener<T>,
) : MutableLiveData<T>(), CoroutineScope {

    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + job
    private var job = Job()
    private val tables = HashSet<String>()
    private lateinit var tracker: InvalidationTracker.Observer
    val loading = MutableLiveData<Boolean>()
    fun invalidate() {
        invalidate(tables)
    }

    init {
        tables.add(table)
        observeTables(table)
    }

    fun observeAlsoTables(vararg moreTables: String) {
        observeTables(table, *moreTables)
    }

    private fun observeTables(table: String, vararg moreTables: String) {
        tables.addAll(moreTables)
        if (::tracker.isInitialized) {
            db.invalidationTracker.removeObserver(tracker)
        }
        tracker = object : InvalidationTracker.Observer(table, *tables.toTypedArray()) {
            override fun onInvalidated(tables: Set<String>) {
                invalidate(tables)
                Log.d("gerwalex", "DatabaseTable invalidated ($tables) in $this@LiveTable")
            }
        }
        db.invalidationTracker.addObserver(tracker)
    }

    /**
     * Lädt Daten.
     *
     * @param tables Namen der geänderten Tabellen.
     */
    protected fun invalidate(tables: Set<String>) {
        launch {
            loading.postValue(true)
            invalidationTracker
                .onInvalidated(tables)
                ?.let {
                    postValue(it)
                }
            loading.postValue(false)
        }
    }

    @CallSuper
    final override fun onActive() {
        super.onActive()
        db.invalidationTracker.addObserver(tracker)
        invalidate(tables)
    }

    final override fun onInactive() {
        super.onInactive()
        db.invalidationTracker.removeObserver(tracker)
    }

    fun interface InvalidationListener<T> {

        /**
         * Lädt Daten.
         *
         * @param tables Namen der geänderten Tabellen. null, wenn direkt [.invalidate]
         * aufgerufen wurde.
         * @return T
         */
        suspend fun onInvalidated(tables: Set<String>): T?
    }
}


