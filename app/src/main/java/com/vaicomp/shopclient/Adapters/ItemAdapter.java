package com.vaicomp.shopclient.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;
import com.vaicomp.shopclient.R;
import com.vaicomp.shopclient.db.AppDataBase;
import com.vaicomp.shopclient.db.CartItem;
import com.vaicomp.shopclient.db.ShopItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {
    private List<ShopItem> itemList;
    private List<CartItem> cartItemList;
    private Activity ctx;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView name, rate, amount;
        private ImageView icon;
        private ElegantNumberButton quantity;

        public MyViewHolder(View convertView) {
            super(convertView);
            name = convertView.findViewById(R.id.itemName);
            icon =  convertView.findViewById(R.id.item_image);
            quantity = convertView.findViewById(R.id.itemQuantity);
            rate = convertView.findViewById(R.id.itemRate);
            amount = convertView.findViewById(R.id.amount);
        }

    }

    @SuppressLint("StaticFieldLeak")
    public ItemAdapter(List<ShopItem> itemList, Activity activity) {
        this.itemList = itemList;
        List<CartItem> cartItemList;
        ctx = activity;
        try {
            final AppDataBase db = Room.databaseBuilder(ctx,
                    AppDataBase.class, "clientAppDB").fallbackToDestructiveMigration().build();
            cartItemList = new AsyncTask<Void, Void, List<CartItem>>() {
                @Override
                protected List<CartItem> doInBackground(Void... voids) {
                    return db.cartItemDao().getAll();
                }
            }.execute().get();
            updateCartDetails(cartItemList, ctx);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @NonNull
    @Override
    public ItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_item_list, parent, false);

        MyViewHolder vh = new MyViewHolder(itemView);
        return vh;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        ShopItem item = itemList.get(position);

        holder.name.setText(item.getItemName());
        holder.quantity.setNumber(String.valueOf(item.getQuantity()));
        holder.rate.setText("Rate: ₹"+ item.getRate());
        holder.amount.setText("₹"+item.getAmount());
        Picasso.get()
                .load(item.getImageUrl())
                .resize(350, 350)
                .centerCrop()
                .into(holder.icon);

        holder.quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                itemList.get(position).setQuantity(Integer.valueOf(holder.quantity.getNumber()));
                itemList.get(position).setAmount(itemList.get(position).getRate() * Integer.parseInt(holder.quantity.getNumber()));

                ShopItem shopItem = itemList.get(position);


                final CartItem cartItem = new CartItem();

                cartItem.setItemId(shopItem.getItemId());
                cartItem.setItemName(shopItem.getItemName());
                cartItem.setImageUrl(shopItem.getImageUrl());
                cartItem.setRate(shopItem.getRate());
                cartItem.setQuantity(Integer.valueOf(holder.quantity.getNumber()));
                cartItem.setAmount(shopItem.getRate() * Integer.parseInt(holder.quantity.getNumber()));


                final AppDataBase db = Room.databaseBuilder(ctx,
                        AppDataBase.class, "clientAppDB").fallbackToDestructiveMigration().build();
                if(cartItem.getQuantity() > 0){

                    try {

                        cartItemList = new AsyncTask<Void, Void, List<CartItem>>() {
                            @Override
                            protected List<CartItem> doInBackground(Void... voids) {
                                db.cartItemDao().insertAll(cartItem);
                                return db.cartItemDao().getAll();
                            }
                        }.execute().get();
                        updateCartDetails(cartItemList, ctx);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                else if(cartItem.getQuantity() == 0){
                    try {
                        cartItemList = new AsyncTask<Void, Void, List<CartItem>>() {
                            @Override
                            protected List<CartItem> doInBackground(Void... voids) {
                                db.cartItemDao().delete(cartItem);
                                return db.cartItemDao().getAll();
                            }
                        }.execute().get();
                        holder.amount.setText("₹ "+cartItem.getAmount());
                        updateCartDetails(cartItemList, ctx);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }

                }


            }
        });
    }

    public static void updateCartDetails(List<CartItem> list, Activity ctx) {
         int itemCount = 0;
         Double amount = (double) 0;

        TextView totalItems, totalamount;
        Toolbar toolbarTop = ctx.findViewById(R.id.toolbar);
        totalItems = toolbarTop.findViewById(R.id.totalItems);
        totalamount = toolbarTop.findViewById(R.id.totalAmount);

        for(CartItem item : list){
            if(item.getQuantity() != 0) {
                itemCount++;
                amount += item.getAmount();
            }
        }

        totalItems.setText(itemCount + " Items");
        totalamount.setText("₹ "+amount);
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }
}