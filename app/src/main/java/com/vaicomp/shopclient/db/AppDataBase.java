package com.vaicomp.shopclient.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ItemList.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract ReceiptDao receiptDao();
}