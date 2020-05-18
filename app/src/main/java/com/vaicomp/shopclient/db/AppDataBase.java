package com.vaicomp.shopclient.db;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.RoomDatabase;

import com.vaicomp.shopclient.ui.home.HomeFragment;

import java.io.Serializable;
import java.util.List;

@Database(entities = {ShopItem.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract ReceiptDao receiptDao();


}


@Dao
interface ReceiptDao {
    @Query("SELECT * FROM ShopItem")
    List<ShopItem> getAll();

    @Insert
    void insertAll(ShopItem... users);

    @Query("DELETE FROM shopitem")
    void nukeTable();
}

