package com.vaicomp.shopclient.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.vaicomp.shopclient.CartActivity;
import com.vaicomp.shopclient.R;
import com.vaicomp.shopclient.db.AppDataBase;
import com.vaicomp.shopclient.db.CartItem;
import com.vaicomp.shopclient.db.OrderModal;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<OrderModal> cartItems;
    private Activity context;


    class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView uname, address, itemsCount, amount, orderedOn, orderState;
        LinearLayout repeatOrder;
        RelativeLayout base;

        HistoryViewHolder(View view) {
            super(view);
            base = view.findViewById(R.id.itemParent);
            base.setOnClickListener(this);

            uname = view.findViewById(R.id.uname);
            address = view.findViewById(R.id.address);
            amount = view.findViewById(R.id.totalAmount);
            itemsCount = view.findViewById(R.id.itemsCount);
            orderedOn = view.findViewById(R.id.orderedOn);
            orderState = view.findViewById(R.id.orderState);

            repeatOrder = view.findViewById(R.id.repeatOrder);
            repeatOrder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == repeatOrder.getId()) {
                repeatOrderFunction(getAdapterPosition());
            } else {
                viewOrderFunction(getAdapterPosition());
            }
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
        final OrderModal item = cartItems.get(position);
        holder.uname.setText(item.getUname());
        holder.address.setText(item.getDeliveryAddress());
        holder.itemsCount.setText(String.valueOf(item.getItemList().size()));

        String pattern = "MMMM dd, yyyy hh:mm a";
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat(pattern, new Locale("en", "IN"));

        holder.orderedOn.setText(simpleDateFormat.format(item.getDate()));
        holder.amount.setText(String.valueOf(item.getGrandTotal()));

        if (item.getState() == 1) {
            holder.orderState.setTextColor(context.getResources().getColor(R.color.normalColor));
            holder.orderState.setText(context.getString(R.string.orderState1));
        } else if (item.getState() == 2) {
            holder.orderState.setTextColor(context.getResources().getColor(R.color.normalColor));
            holder.orderState.setText(context.getString(R.string.orderState2));
        } else if (item.getState() == 3) {
            holder.orderState.setTextColor(context.getResources().getColor(R.color.normalColor));
            holder.orderState.setText(context.getString(R.string.orderState3));
        } else if (item.getState() == 4) {
            holder.orderState.setText(context.getString(R.string.orderState4));
            holder.orderState.setTextColor(context.getResources().getColor(R.color.successColor));
        }
        else if(item.getState() == 0){
            holder.orderState.setText("Order Canceled");
            holder.orderState.setTextColor(context.getResources().getColor(R.color.errorColor));
        }

    }

    @SuppressLint("StaticFieldLeak")
    private void repeatOrderFunction(final int pos) {
        final AppDataBase db = Room.databaseBuilder(context,
                AppDataBase.class, "clientAppDB").fallbackToDestructiveMigration().build();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                db.cartItemDao().nukeTable();
                int i = 0;
                CartItem[] itemArray = new CartItem[cartItems.get(pos).getItemList().size()];
                for (CartItem cartItem : cartItems.get(pos).getItemList()) {
                    itemArray[i] = cartItem;
                    i++;
                }

                db.cartItemDao().insertAll(itemArray);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                context.startActivity(new Intent(context, CartActivity.class)
                        .putExtra("ORDER_ID", "NA"));
            }
        }.execute();
    }

    private void viewOrderFunction(final int pos) {
        context.startActivity(new Intent(context, CartActivity.class)
                .putExtra("ORDER_ID", cartItems.get(pos).getOrderId()));
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