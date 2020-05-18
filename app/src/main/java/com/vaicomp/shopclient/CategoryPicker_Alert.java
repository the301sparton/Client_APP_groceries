package com.vaicomp.shopclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.vaicomp.shopclient.Adapters.CategoryAdapter;
import com.vaicomp.shopclient.db.AppDataBase;
import com.vaicomp.shopclient.db.CategoryFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CategoryPicker_Alert extends Activity {

    Context context;
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_category_picker);
        context = getApplicationContext();
        this.setFinishOnTouchOutside(false);

        final AppDataBase db = Room.databaseBuilder(getApplicationContext(),
                AppDataBase.class, "clientAppDB").fallbackToDestructiveMigration().build();
        List<CategoryFilter> categoryFilterList = new ArrayList<>();
        try {
            categoryFilterList = new AsyncTask<Void, Void, List<CategoryFilter>>() {
                @Override
                protected List<CategoryFilter> doInBackground(Void... voids) {
                    return db.categoryFilterDao().getAll();
                }
            }.execute().get();

            RecyclerView categoryList = findViewById(R.id.categoryList);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            categoryList.setLayoutManager(mLayoutManager);
            categoryList.setItemAnimator(new DefaultItemAnimator());
            CategoryAdapter adapter = new CategoryAdapter(categoryFilterList);
            categoryList.setAdapter(adapter);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

