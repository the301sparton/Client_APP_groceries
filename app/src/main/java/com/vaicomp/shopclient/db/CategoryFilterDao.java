package com.vaicomp.shopclient.db;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface CategoryFilterDao {
    @Query("SELECT * FROM categoryfilter")
    List<CategoryFilter> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(CategoryFilter... users);

    @Query("DELETE FROM categoryfilter")
    void nukeTable();

    @Delete
    void delete(CategoryFilter user);
}
