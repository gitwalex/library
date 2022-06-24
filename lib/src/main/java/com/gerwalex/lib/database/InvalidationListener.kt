package com.gerwalex.lib.database

fun interface InvalidationListener<T> {

    /**
     * Lädt Daten.
     *
     * @param tables Namen der geänderten Tabellen. null, wenn direkt [.invalidate]
     * aufgerufen wurde.
     * @return T
     */
    fun onInvalidated(tables: Set<String>): T?
}