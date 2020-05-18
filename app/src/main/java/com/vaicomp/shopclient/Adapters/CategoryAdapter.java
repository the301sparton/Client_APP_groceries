package com.vaicomp.shopclient.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.vaicomp.shopclient.R;
import com.vaicomp.shopclient.db.AppDataBase;
import com.vaicomp.shopclient.db.CategoryFilter;
import net.igenius.customcheckbox.CustomCheckBox;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<CategoryFilter> categoryFilterList;
    private Context context;
    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        CustomCheckBox aSwitch;

        CategoryViewHolder(View view) {
            super(view);
            categoryName = view.findViewById(R.id.categoryName);
            aSwitch = view.findViewById(R.id.categorySwitch);
        }
    }


    public CategoryAdapter(List<CategoryFilter> moviesList, Context context) {
        this.categoryFilterList = moviesList;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_filter_item_list, parent, false);

        return new CategoryViewHolder(itemView);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onBindViewHolder(CategoryViewHolder holder, final int position) {
        CategoryFilter item = categoryFilterList.get(position);
        holder.categoryName.setText(item.getName());
        holder.aSwitch.setChecked(item.getEnabled());

        holder.aSwitch.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                categoryFilterList.get(position).setEnabled(isChecked);

                final AppDataBase db = Room.databaseBuilder(context,
                        AppDataBase.class, "clientAppDB").fallbackToDestructiveMigration().build();

                final CategoryFilter categoryFilter = categoryFilterList.get(position);

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        db.categoryFilterDao().insertAll(categoryFilter);
                        return null;
                    }
                }.execute();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryFilterList.size();
    }
}