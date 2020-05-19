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
    FirebaseFirestore fdb;
    OrderModal omGlobal;
    Button placeOrderBtn;
    String order_id;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_cart);
        setTitle("Order Summary");


        order_id = getIntent().getStringExtra("ORDER_ID");

        fdb = FirebaseFirestore.getInstance();
        placeOrderBtn = findViewById(R.id.placeOrderBtn);

        initViews(order_id);

        fdb.collection("shopDetails").document("/d1ajtkwauTOe8z27xdH8").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                TextView tv = findViewById(R.id.shopName);
                final String shopName = String.valueOf(documentSnapshot.get("shopName"));
                tv.setText(shopName);

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




               placeOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Context context = getApplicationContext();
                        if(order_id.equals("NA")){

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

                                            Toasty.success(context, "Order Placed Successfully!", Toasty.LENGTH_SHORT).show();
                                            initViews(orderModal.getOrderId());
                                        }
                                    }.execute();
                                }
                            });
                        }

                        else if(omGlobal.getState() <= 2){
                            fdb.collection("orders").document(order_id)
                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toasty.success(context, "Order Canceled Successfully", Toasty.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }

                    }
                });

            }


        });
    }

    @SuppressLint("StaticFieldLeak")
    private void initViews(final String order_idT) {
        if (!order_idT.equals("NA")) {
            db = Room.databaseBuilder(getApplicationContext(),
                    AppDataBase.class, "clientAppDB").fallbackToDestructiveMigration().build();
            final List<CartItem> categoryFilterList = new ArrayList<>();
            fdb.collection("orders").document(order_idT).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    OrderModal orderModal = documentSnapshot.toObject(OrderModal.class);
                    omGlobal = orderModal;
                    order_id = documentSnapshot.getId();
                    RecyclerView categoryList = findViewById(R.id.list);
                    RecyclerView.LayoutManager mLayoutManager = new CustomGridLayoutManager(getApplicationContext());
                    categoryList.setLayoutManager(mLayoutManager);
                    categoryList.setItemAnimator(new DefaultItemAnimator());

                    adapter = new CartAdapter(orderModal.getItemList(), orderModal.getState(), CartActivity.this);
                    categoryList.setAdapter(adapter);

                    for (CartItem item : categoryFilterList) {
                        amount += item.getAmount();
                    }
                    TextView tv = findViewById(R.id.itemTotal);
                    tv.setText(String.valueOf(amount));

                    tv = findViewById(R.id.orderState);
                    if (orderModal.getState() == 1) {
                        placeOrderBtn.setText("Cancel Order");
                        tv.setText(getString(R.string.orderState1));
                    } else if (orderModal.getState() == 2) {
                        placeOrderBtn.setText("Cancel Order");
                        tv.setText(getString(R.string.orderState2));
                    } else if (orderModal.getState() == 3) {
                        placeOrderBtn.setText("Order WIll Arrive Shortly");
                        tv.setText(getString(R.string.orderState3));
                    } else if (orderModal.getState() == 4) {
                        placeOrderBtn.setText("Order WIll Arrive Shortly");
                        tv.setText(getString(R.string.orderState4));
                    }

                }
            });
        } else {
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

                adapter = new CartAdapter(categoryFilterList, 0, CartActivity.this);
                categoryList.setAdapter(adapter);

                for (CartItem item : categoryFilterList) {
                    amount += item.getAmount();
                }
                TextView totalAmount = findViewById(R.id.itemTotal);
                totalAmount.setText(String.valueOf(amount));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            placeOrderBtn.setText(R.string.placeOrder);
            TextView orderState = findViewById(R.id.orderState);
            orderState.setText(R.string.orderInCart);
        }
    }

    public static class CustomGridLayoutManager extends LinearLayoutManager {
        public CustomGridLayoutManager(Context context) {
            super(context);
        }

        @Override
        public boolean canScrollVertically() {
            return false;
        }
    }
}
