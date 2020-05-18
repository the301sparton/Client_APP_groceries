package com.vaicomp.shopclient.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.vaicomp.shopclient.R;
import com.vaicomp.shopclient.db.CategoryFilter;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<CategoryFilter> categoryFilterList;

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        CustomCheckBox aSwitch;

        CategoryViewHolder(View view) {
            super(view);
            categoryName = view.findViewById(R.id.categoryName);
            aSwitch = view.findViewById(R.id.categorySwitch);
        }
    }


    public CategoryAdapter(List<CategoryFilter> moviesList) {
        this.categoryFilterList = moviesList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_filter_item_list, parent, false);

        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        CategoryFilter item = categoryFilterList.get(position);
        holder.categoryName.setText(item.getName());
        holder.aSwitch.setChecked(item.getEnabled());
    }

    @Override
    public int getItemCount() {
        return categoryFilterList.size();
    }
}