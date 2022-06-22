package com.gerwalex.lib.database

abstract class InvalidationListener<T> {

    /**
     * Lädt Daten.
     *
     * @param tables Namen der geänderten Tabellen. null, wenn direkt [.invalidate]
     * aufgerufen wurde.
     * @return T
     */
    abstract fun onInvalidated(tables: Set<String>): T?
}