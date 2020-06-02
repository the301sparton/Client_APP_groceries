package com.vaicomp.shopclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.vaicomp.shopclient.Adapters.CategoryAdapter;
import com.vaicomp.shopclient.Adapters.CategoryGridAdapter;
import com.vaicomp.shopclient.db.AppDataBase;
import com.vaicomp.shopclient.db.CategoryFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CategoryGridActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_category_grid);

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

            Collections.sort(categoryFilterList, new Comparator<CategoryFilter>() {
                @Override
                public int compare(CategoryFilter o1, CategoryFilter o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            RecyclerView gridView = findViewById(R.id.grid_view);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
            gridView.setLayoutManager(mLayoutManager);
            gridView.setItemAnimator(new DefaultItemAnimator());

           gridView.setHasFixedSize(false);
            CategoryGridAdapter adapter = new CategoryGridAdapter(CategoryGridActivity.this, categoryFilterList);
            gridView.setAdapter(adapter);


            LinearLayout skipBar = findViewById(R.id.skipBar);

            skipBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CategoryGridActivity.this, HomeActivity.class));
                }
            });

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
// class SpacesItemDecoration extends RecyclerView.ItemDecoration {
//    private int space;
//
//    public SpacesItemDecoration(int space) {
//        this.space = space;
//    }
//
//    @Override
//    public void getItemOffsets(Rect outRect, View view,
//                               RecyclerView parent, RecyclerView.State state) {
//       outRect.bottom = 20;
//
//        // Add top margin only for the first item to avoid double space between items
//        if (parent.getChildLayoutPosition(view) == 0) {
//            outRect.top = space;
//        } else {
//            outRect.top = 0;
//        }
//    }
//}