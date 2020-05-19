package com.vaicomp.shopclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vaicomp.shopclient.Adapters.CartAdapter;
import com.vaicomp.shopclient.db.AppDataBase;
import com.vaicomp.shopclient.db.CartItem;
import com.vaicomp.shopclient.db.OrderModal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import es.dmoral.toasty.Toasty;

public class CartActivity extends AppCompatActivity {

    double amount = 0;
    double deliveryCharge;
    CartAdapter adapter;
    AppDataBase db;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_cart);
        setTitle("Order Summary");

        final String order_id = getIntent().getStringExtra("ORDER_ID");
        if (order_id.equals("NA")) {

            db = Room.databaseBuilder(getApplicationContext(),
                    AppDataBase.class, "clientAppDB").fallbackToDestructiveMigration().build();
            List<CartItem> categoryFilterList = new ArrayList<>();
            try {
                categoryFilterList = new AsyncTask<Void, Void, List<CartItem>>() {
                    @Override
                    protected List<CartItem> doInBackground(Void... voids) {
                        return db.cartItemDao().getAll();
                    }
                }.execute().get();

                RecyclerView categoryList = findViewById(R.id.list);
                RecyclerView.LayoutManager mLayoutManager = new CustomGridLayoutManager(getApplicationContext());
                categoryList.setLayoutManager(mLayoutManager);
                categoryList.setItemAnimator(new DefaultItemAnimator());

                adapter = new CartAdapter(categoryFilterList, CartActivity.this);
                categoryList.setAdapter(adapter);

                for(CartItem item : categoryFilterList){
                    amount += item.getAmount();
                }
                TextView totalAmount = findViewById(R.id.itemTotal);
                totalAmount.setText(String.valueOf(amount));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            TextView orderState = findViewById(R.id.orderState);
            orderState.setText(R.string.orderInCart);
        }

        final FirebaseFirestore fdb = FirebaseFirestore.getInstance();
        fdb.collection("shopDetails").document("/d1ajtkwauTOe8z27xdH8").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                TextView tv = findViewById(R.id.shopName);
                tv.setText(String.valueOf(documentSnapshot.get("shopName")));

                tv = findViewById(R.id.shopAddress);
                tv.setText(String.valueOf(documentSnapshot.get("address")));

                tv = findViewById(R.id.dAddressValue);
                tv.setText(preferenceManager.getAdress(getApplicationContext()));

                tv = findViewById(R.id.phoneNumberValue);
                tv.setText(preferenceManager.getPhoneNumber(getApplicationContext()));

                tv = findViewById(R.id.deliveryCharges);

                deliveryCharge = Double.parseDouble(String.valueOf(documentSnapshot.get("deliveryCharge")));
                tv.setText(String.valueOf(deliveryCharge));

                tv = findViewById(R.id.totalAmount);
                tv.setText(String.valueOf(amount + deliveryCharge));

                Button placeOrderBtn = findViewById(R.id.placeOrderBtn);
                placeOrderBtn.setVisibility(View.VISIBLE);

                if(order_id.equals("NA")){
                    placeOrderBtn.setText(R.string.placeOrder);
                    placeOrderBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Context context = getApplicationContext();
                            final OrderModal orderModal = new OrderModal();
                            orderModal.setUid(preferenceManager.getUID(context));
                            orderModal.setShopId("d1ajtkwauTOe8z27xdH8");
                            orderModal.setUname(preferenceManager.getDisplayName(context));
                            orderModal.setPhoneNumber(preferenceManager.getPhoneNumber(context));
                            orderModal.setDeliveryAddress(preferenceManager.getAdress(context));
                            orderModal.setDate(new Date());

                            orderModal.setItemList(adapter.getAdapterData());
                            orderModal.setState(1);
                            orderModal.setDeliveryCost(deliveryCharge);
                            orderModal.setItemTotal(amount);
                            orderModal.setGrandTotal(amount + deliveryCharge);

                            fdb.collection("orders").add(orderModal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    orderModal.setOrderId(documentReference.getId());
                                    new AsyncTask<Void, Void, Void>() {
                                        @Override
                                        protected Void doInBackground(Void... voids) {
                                            db.cartItemDao().nukeTable();
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Void aVoid) {
                                            super.onPostExecute(aVoid);
                                            TextView tv = findViewById(R.id.orderNumber);
                                            tv.setText(orderModal.getOrderId());

                                            tv = findViewById(R.id.Date);
                                            String pattern = "MMMM dd, yyyy hh:mm a";
                                            SimpleDateFormat simpleDateFormat =
                                                    new SimpleDateFormat(pattern, new Locale("en", "IN"));
                                            tv.setText(simpleDateFormat.format(orderModal.getDate()));

                                            Toasty.success(context, "Order Placed Successfully!",Toasty.LENGTH_SHORT).show();
                                        }
                                    }.execute();
                                }
                            });
                        }
                    });
                }
            }
        });


    }


    public class CustomGridLayoutManager extends LinearLayoutManager {
        public CustomGridLayoutManager(Context context) {
            super(context);
        }
        @Override
        public boolean canScrollVertically() {
             return false;
        }
    }
}
