package com.vaicomp.shopclient.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.firebase.firestore.auth.User;

import java.util.List;

@Dao
public interface CartItemDao {
    @Query("SELECT * FROM cartitem")
    List<CartItem> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(CartItem... users);

    @Query("DELETE FROM cartitem")
    void nukeTable();

    @Delete
    void delete(CartItem user);
}
