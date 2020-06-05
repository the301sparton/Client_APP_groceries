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

    @Query("UPDATE categoryfilter SET isEnabled = :isEnabled WHERE name = :name")
    void updateByName(String name, boolean isEnabled);

    @Query("SELECT * FROM categoryfilter WHERE isEnabled = 1")
    List<CategoryFilter> getEnabled();

    @Query("UPDATE categoryfilter SET isEnabled = 0")
    void removeAllFilters();

    @Delete
    void delete(CategoryFilter user);
}
