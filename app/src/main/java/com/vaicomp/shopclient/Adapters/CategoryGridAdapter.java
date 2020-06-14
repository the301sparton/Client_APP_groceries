package com.vaicomp.shopclient.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.vaicomp.shopclient.HomeActivity;
import com.vaicomp.shopclient.R;
import com.vaicomp.shopclient.db.AppDataBase;
import com.vaicomp.shopclient.db.CategoryFilter;

import java.util.List;

public class CategoryGridAdapter extends RecyclerView.Adapter<CategoryGridAdapter.CategoryGridViewHolder> {
    private Activity mContext;
    List<CategoryFilter> categoryList;

    public CategoryGridAdapter(Activity activity, List<CategoryFilter> categoryList){
        this.mContext = activity;
        this.categoryList = categoryList;
        Log.i("LENGTH", String.valueOf(categoryList.size()));
    }

    static class CategoryGridViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        CategoryGridViewHolder(View view) {
            super(view);
            categoryName = view.findViewById(R.id.categoryName);
        }
    }

    @NonNull
    @Override
    public CategoryGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_grid_item, parent, false);
        return new CategoryGridAdapter.CategoryGridViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryGridViewHolder holder, final int position) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if(position%2==0){
            Log.i("box width", String.valueOf((displayMetrics.widthPixels / 2) - 50));
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((displayMetrics.widthPixels / 2) - 60, 350);
            params.gravity = Gravity.RIGHT;
            holder.categoryName.setLayoutParams(params);
        }else{
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((displayMetrics.widthPixels / 2) - 60, 350);
            params.gravity = Gravity.LEFT;
            holder.categoryName.setLayoutParams(params);
        }
        holder.categoryName.setText(categoryList.get(position).getName());
        holder.categoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressLint("StaticFieldLeak")
            public void onClick(View v) {
                final AppDataBase db = Room.databaseBuilder(mContext,
                        AppDataBase.class, "clientAppDB").fallbackToDestructiveMigration().build();

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        db.categoryFilterDao().removeAllFilters();
                        db.categoryFilterDao().updateByName(categoryList.get(position).getName(), true);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        mContext.startActivity(new Intent(mContext, HomeActivity.class));
                    }
                }.execute();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


}


