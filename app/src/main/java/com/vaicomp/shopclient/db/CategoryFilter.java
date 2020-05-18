package com.vaicomp.shopclient.db;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CategoryFilter {
    @PrimaryKey
    @NonNull
    private String name;

    @ColumnInfo(name = "isEnabled")
    private Boolean isEnabled;

    public CategoryFilter(String name, Boolean isEnabled){
        this.name = name;
        this.isEnabled = isEnabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }
}
