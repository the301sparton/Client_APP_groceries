package com.vaicomp.shopclient.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.squareup.picasso.Picasso;
import com.vaicomp.shopclient.R;
import com.vaicomp.shopclient.db.AppDataBase;
import com.vaicomp.shopclient.db.CartItem;
import com.vaicomp.shopclient.db.CategoryFilter;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private Activity context;

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, rateQ, amount;
        ImageView itemImage, delteBtn;

        CartViewHolder(View view) {
            super(view);
            itemName = view.findViewById(R.id.itemName);
            rateQ = view.findViewById(R.id.rateQ);
            amount = view.findViewById(R.id.amount);
           // itemImage = view.findViewById(R.id.item_image);
            delteBtn = view.findViewById(R.id.deleteItem);
        }
    }


    public CartAdapter(List<CartItem> moviesList, Activity context) {
        this.cartItems = moviesList;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item_list, parent, false);

        return new CartViewHolder(itemView);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onBindViewHolder(CartViewHolder holder, final int position) {
        CartItem item = cartItems.get(position);
        holder.itemName.setText(item.getItemName());
        holder.rateQ.setText(item.getQuantity() + " X ₹"+item.getRate());
        holder.amount.setText("₹"+item.getRate()*item.getQuantity());
//        Picasso.get()
//                .load(item.getImageUrl())
//                .resize(350, 350)
//                .centerCrop()
//                .into(holder.itemImage);

        holder.delteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CartItem item = cartItems.get(position);
                final AppDataBase db = Room.databaseBuilder(context,
                        AppDataBase.class, "clientAppDB").fallbackToDestructiveMigration().build();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        db.cartItemDao().delete(item);
                        cartItems.clear();
                        cartItems.addAll(db.cartItemDao().getAll());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        notifyDataSetChanged();
                        if(cartItems.size() == 0){
                            context.finish();
                        }
                    }
                }.execute();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}