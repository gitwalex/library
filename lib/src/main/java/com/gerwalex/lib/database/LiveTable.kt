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
import java.util.*
import kotlin.coroutines.CoroutineContext

abstract class LiveTable<T>(private val db: RoomDatabase, table: String, vararg moreTables: String?) :
    MutableLiveData<T>(), CoroutineScope {
    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + job
    private lateinit var job: Job

    protected val tables: MutableSet<String>
    private val tracker: InvalidationTracker.Observer

    @JvmField
    val loading = MutableLiveData<Boolean>()
    fun invalidate() {
        invalidate(tables)
    }

    init {
        tracker = object : InvalidationTracker.Observer(table, *moreTables) {
            override fun onInvalidated(tables: Set<String>) {
                invalidate(tables)
                Log.d(
                    "gerwalex",
                    String.format("DatabaseTable invalidated (%1s) in %2s", tables, this@LiveTable)
                )
            }
        }
        tables = HashSet(Arrays.asList(*moreTables))
        tables.add(table)
    }


    /**
     * Lädt Daten.
     *
     * @param tables Namen der geänderten Tabellen.
     */
    protected fun invalidate(tables: Set<String>) {
        launch {
            loading.postValue(true)
            postValue(onInvalidated(tables))
            loading.postValue(false)
        }
    }


    /**
     * Lädt Daten.
     *
     * @param tables Namen der geänderten Tabellen. null, wenn direkt [.invalidate]
     * aufgerufen wurde.
     * @return T
     */
    protected abstract fun onInvalidated(tables: Set<String>): T
    final override fun observeForever(observer: Observer<in T>) {
        throw IllegalStateException(
            "observeForever() zur Vermeidung von MemoryLeaks nicht möglich. Datenbank erhält Referenz auf " +
                    javaClass.name
        )
    }

    @CallSuper
    final override fun onActive() {
        super.onActive()
        job = Job()
        db.invalidationTracker.addObserver(tracker)
        invalidate(tables)
    }

    final override fun onInactive() {
        super.onInactive()
        db.invalidationTracker.removeObserver(tracker)
        job.cancel()
    }
}


