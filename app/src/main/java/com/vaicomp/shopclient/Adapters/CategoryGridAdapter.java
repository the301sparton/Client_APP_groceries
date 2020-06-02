package com.vaicomp.shopclient.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vaicomp.shopclient.R;
import com.vaicomp.shopclient.db.CategoryFilter;

import java.util.List;

public class CategoryGridAdapter extends RecyclerView.Adapter<CategoryGridAdapter.CategoryGridViewHolder> {
    private Context mContext;
    List<CategoryFilter> categoryList;

    public CategoryGridAdapter(Context activity, List<CategoryFilter> categoryList){
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
    public void onBindViewHolder(@NonNull CategoryGridViewHolder holder, int position) {
        if(position%2==0){
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(450, 350);
            params.gravity = Gravity.RIGHT;
            holder.categoryName.setLayoutParams(params);
        }else{
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(450, 350);
            params.gravity = Gravity.LEFT;
            holder.categoryName.setLayoutParams(params);
        }
        holder.categoryName.setText(categoryList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


}


