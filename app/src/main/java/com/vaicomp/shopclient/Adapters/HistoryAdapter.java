package com.vaicomp.shopclient.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vaicomp.shopclient.R;
import com.vaicomp.shopclient.db.OrderModal;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<OrderModal> cartItems;
    private Activity context;

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView uname, address, itemsCount, amount, orderedOn, orderState;

        HistoryViewHolder(View view) {
            super(view);
            uname = view.findViewById(R.id.uname);
            address = view.findViewById(R.id.address);
            amount = view.findViewById(R.id.totalAmount);
            itemsCount = view.findViewById(R.id.itemsCount);
            orderedOn = view.findViewById(R.id.orderedOn);
            orderState = view.findViewById(R.id.orderState);
        }
    }


    public HistoryAdapter(List<OrderModal> moviesList, Activity context) {
        this.cartItems = moviesList;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item_list, parent, false);

        return new HistoryViewHolder(itemView);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onBindViewHolder(HistoryViewHolder holder, final int position) {
        OrderModal item = cartItems.get(position);
        holder.uname.setText(item.getUname());
        holder.address.setText(item.getDeliveryAddress());
        holder.itemsCount.setText(String.valueOf(item.getItemList().size()));
        holder.orderedOn.setText(item.getDeliveryAddress());
        holder.amount.setText(String.valueOf(item.getGrandTotal()));

        if(item.getState() == 1){
            holder.orderState.setText(context.getString(R.string.orderState1));
        }
        else if(item.getState() == 2){
            holder.orderState.setText(context.getString(R.string.orderState2));
        }
        else if(item.getState() == 3){
            holder.orderState.setText(context.getString(R.string.orderState3));
        }
        else if(item.getState() == 4){
            holder.orderState.setText(context.getString(R.string.orderState4));
        }
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