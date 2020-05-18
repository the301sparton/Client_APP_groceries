package com.vaicomp.shopclient.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;
import com.vaicomp.shopclient.R;
import com.vaicomp.shopclient.ui.home.HomeFragment;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {
    private List<HomeFragment.ShopItem> itemList;
    private Activity ctx;

    private int itemCount = 0;
    private Double amount = (double) 0;

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

    public ItemAdapter(List<HomeFragment.ShopItem> itemList, Activity activity) {
        this.itemList = itemList;
        ctx = activity;
    }


    @Override
    public ItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_item_list, parent, false);

        MyViewHolder vh = new MyViewHolder(itemView);
        return vh;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Log.i(position + " ==> " , itemList.get(position).getItemName());

        HomeFragment.ShopItem item = itemList.get(position);
        holder.name.setText(item.getItemName());
        holder.quantity.setNumber(String.valueOf(item.getQuantity()));
        holder.rate.setText(String.valueOf(item.getRate()));
        holder.amount.setText(item.getAmount() + " Rs.");
        Picasso.get()
                .load(item.getImageUrl())
                .resize(350, 350)
                .centerCrop()
                .into(holder.icon);

        holder.quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                itemList.get(position).setAmount( itemList.get(position).getRate() * newValue);
                itemList.get(position).setQuantity(Integer.valueOf(view.getNumber()));
                Log.i("Position", itemList.get(position).getImageUrl());

                Log.i("Position", String.valueOf(position));
                holder.amount.setText(itemList.get(position).getAmount() + " Rs,");
                updateCartDetails(itemList);
            }
        });
    }

    private void updateCartDetails(List<HomeFragment.ShopItem> list) {

        TextView totalItems, totalamount;
        Toolbar toolbarTop = ctx.findViewById(R.id.toolbar);
        totalItems = toolbarTop.findViewById(R.id.totalItems);
        totalamount = toolbarTop.findViewById(R.id.totalAmount);

        for(HomeFragment.ShopItem item : list){
            if(item.getQuantity() > 0) {
                itemCount++;
                amount += item.getAmount();
            }
        }
        if(itemCount > 1){
            totalItems.setText(itemCount + " Items");
        }
        else{
            totalItems.setText(itemCount + " Item");
        }

        totalamount.setText(amount + " Rs.");
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }
}