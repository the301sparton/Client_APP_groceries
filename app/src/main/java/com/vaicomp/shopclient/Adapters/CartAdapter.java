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
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.vaicomp.shopclient.R;
import com.vaicomp.shopclient.db.AppDataBase;
import com.vaicomp.shopclient.db.CartItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private int type;
    private double dMargin, deliveryCharge,deliveryChargeOriginal;
    private Activity context;

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, rateQ, amount;
        ImageView delteBtn;

        CartViewHolder(View view) {
            super(view);
            itemName = view.findViewById(R.id.itemName);
            rateQ = view.findViewById(R.id.rateQ);
            amount = view.findViewById(R.id.amount);
            delteBtn = view.findViewById(R.id.deleteItem);
        }
    }


    public CartAdapter(List<CartItem> moviesList, int type,double dMargin, double deliveryCharge, Activity context) {
        this.cartItems = moviesList;
        this.deliveryChargeOriginal = deliveryCharge;
        this.context = context;
        this.dMargin = dMargin;
        this.type = type;
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
        holder.rateQ.setText(MessageFormat.format("{0} X ₹ {1}", item.getQuantity(), round(item.getRate(),2)));
        holder.amount.setText(String.format("₹ %s", round(item.getRate() * item.getQuantity(),2)));


        if(type == -1) {
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
                            if (cartItems.size() == 0) {
                                context.finish();
                            }
                            else{
                                double amount = 0;
                                for(CartItem orderModal : cartItems){
                                    amount += orderModal.getAmount();
                                }
                                amount = round(amount,2);

                                if(amount >= dMargin){
                                    deliveryCharge = 0d;
                                }
                                else{
                                    deliveryCharge = deliveryChargeOriginal;
                                }
                                TextView tvD = context.findViewById(R.id.deliveryCharges);
                                tvD.setText(String.valueOf(deliveryCharge));

                                TextView tv = context.findViewById(R.id.itemTotal);
                                tv.setText(MessageFormat.format("₹ {0}", amount));

                                tv = context.findViewById(R.id.totalAmount);
                                tv.setText(String.format("₹ %s", amount + Double.parseDouble(String.valueOf(tvD.getText()))));
                            }
                        }
                    }.execute();
                }
            });
        }
        else{
            holder.delteBtn.setVisibility(View.GONE);
        }
    }

    public List<CartItem> getAdapterData(){
        return  cartItems;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
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