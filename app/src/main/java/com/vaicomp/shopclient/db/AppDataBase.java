package com.vaicomp.shopclient.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ShopItem.class, CartItem.class, CategoryFilter.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract ShopItemDao shopItemDao();
    public abstract CartItemDao cartItemDao();
    public abstract CategoryFilterDao categoryFilterDao();
}


