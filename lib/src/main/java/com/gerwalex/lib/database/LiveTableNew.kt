package com.gerwalex.lib.database

import android.util.Log
import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

open class LiveTableNew<T>(
    private val db: RoomDatabase, table: String,
    private val invalidationTracker: InvalidationListener<T>,
) : MutableLiveData<T>(), CoroutineScope {

    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + job
    private var job = Job()
    private val table: String
    private lateinit var tables: HashSet<String>
    private var tracker: InvalidationTracker.Observer
    val loading = MutableLiveData<Boolean>()
    fun invalidate() {
        invalidate(tables)
    }

    init {
        this.table = table
        tracker = object : InvalidationTracker.Observer(table) {
            override fun onInvalidated(tables: Set<String>) {
                invalidate(tables)
                Log.d("gerwalex", "DatabaseTable invalidated ($tables) in $this@LiveTable")
            }
        }
    }

    fun observeAlsoTables(vararg moreTables: String) {
        tables.addAll(moreTables)
        db.invalidationTracker.removeObserver(tracker)
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

    final override fun observeForever(observer: Observer<in T>) {
        throw IllegalStateException(
            "observeForever() zur Vermeidung von MemoryLeaks nicht möglich. Datenbank erhält Referenz auf " +
                    javaClass.name
        )
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
}


