package com.vaicomp.shopclient.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ShopItemDao {
    @Query("SELECT * FROM ShopItem")
    List<ShopItem> getAll();

    @Insert
    void insertAll(ShopItem... users);

    @Query("DELETE FROM shopitem")
    void nukeTable();
}
