package com.gerwalex.lib.database;

import androidx.room.Dao;
import androidx.room.RoomDatabase;
import androidx.room.Transaction;

import java.util.concurrent.Callable;

@Dao
public abstract class DBDao {
    protected final RoomDatabase db;

    public DBDao(RoomDatabase db) {
        this.db = db;
    }

    @Transaction
    public Long runInTransaction(Callable<Long> c) {
        try {
            return c.call();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Transaction canceled", e);
        }
    }
}
