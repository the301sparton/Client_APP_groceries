package com.vaicomp.shopclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
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

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    ScrollView baseView;
    LinearLayout loader;
    ImageView editProfileBtn;
    Spinner slot;
    ArrayList<String> spinnerArray;


    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_cart);
        setTitle("Order Summary");


        baseView = findViewById(R.id.baseView);
        loader = findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);
        baseView.setVisibility(View.GONE);
        slot = findViewById(R.id.slotValue);
        order_id = getIntent().getStringExtra("ORDER_ID");

        fdb = FirebaseFirestore.getInstance();
        placeOrderBtn = findViewById(R.id.placeOrderBtn);


        fdb.collection("shopDetails").document("/d1ajtkwauTOe8z27xdH8").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
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

                editProfileBtn = findViewById(R.id.editProfile);


                spinnerArray = new ArrayList<>();
                spinnerArray.add("Click ME!");
                spinnerArray = (ArrayList<String>) documentSnapshot.get("slots");
                spinnerArray.add("Click ME!");
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
                        (getApplicationContext(), android.R.layout.simple_spinner_item,
                                spinnerArray);
                slot.setAdapter(spinnerArrayAdapter);
                slot.setSelection(spinnerArray.size() - 1);

                initViews(order_id);



                placeOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        placeOrderBtn.setEnabled(false);
                        final Context context = getApplicationContext();
                        if(order_id.equals("NA")){
                            if(!slot.getSelectedItem().toString().equals("Click ME!")){
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
                                orderModal.setOrderSlot(slot.getSelectedItem().toString());

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
                                                placeOrderBtn.setEnabled(true);
                                            }
                                        }.execute();
                                    }
                                });
                            }
                            else{
                                placeOrderBtn.setEnabled(true);
                                Toasty.error(context, "Please Select Slot!",Toasty.LENGTH_SHORT).show();
                            }
                        }

                        else if(omGlobal.getState() == 1){
                            loader.setVisibility(View.VISIBLE);
                            baseView.setVisibility(View.GONE);
                            fdb.collection("orders").document(order_id)
                                    .update("state",0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    placeOrderBtn.setEnabled(true);
                                    baseView.setVisibility(View.VISIBLE);
                                    loader.setVisibility(View.GONE);
                                    Toasty.success(context, "Order Canceled Successfully", Toasty.LENGTH_SHORT).show();
                                    initViews(omGlobal.getOrderId());
                                }
                            });
                        }

                        else if(omGlobal.getState() == 2){
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + documentSnapshot.get("contactNumber")));
                            startActivity(callIntent);
                        }

                        else if(omGlobal.getState() == 3){
                            loader.setVisibility(View.VISIBLE);
                            baseView.setVisibility(View.GONE);
                            fdb.collection("orders").document(order_id).update("state",4).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toasty.success(getApplicationContext(), "Order Received",Toasty.LENGTH_SHORT).show();
                                    placeOrderBtn.setEnabled(false);
                                    baseView.setVisibility(View.VISIBLE);
                                    loader.setVisibility(View.GONE);
                                    initViews(omGlobal.getOrderId());
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
             editProfileBtn.setVisibility(View.GONE);
             db = Room.databaseBuilder(getApplicationContext(),
                    AppDataBase.class, "clientAppDB").fallbackToDestructiveMigration().build();
            fdb.collection("orders").document(order_idT).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    OrderModal orderModal = documentSnapshot.toObject(OrderModal.class);
                    omGlobal = orderModal;
                    omGlobal.setOrderId(documentSnapshot.getId());
                    order_id = documentSnapshot.getId();

                    RecyclerView categoryList = findViewById(R.id.list);
                    RecyclerView.LayoutManager mLayoutManager = new CustomGridLayoutManager(getApplicationContext());
                    categoryList.setLayoutManager(mLayoutManager);
                    categoryList.setItemAnimator(new DefaultItemAnimator());

                    int position = 0, i = 0;
                    for(String slotName : spinnerArray){
                        if(slotName.equals(orderModal.getOrderSlot())){
                            position = i;
                            break;
                        }
                        i++;
                    }
                    slot.setSelection(position);
                    slot.setEnabled(false);

                    adapter = new CartAdapter(orderModal.getItemList(), orderModal.getState(), CartActivity.this);
                    categoryList.setAdapter(adapter);


                    TextView tv = findViewById(R.id.itemTotal);
                    tv.setText(MessageFormat.format("₹ {0}", CartAdapter.round(orderModal.getItemTotal(),2)));

                    tv = findViewById(R.id.totalAmount);
                    tv.setText(String.format("₹ %s", CartAdapter.round((orderModal.getItemTotal() + orderModal.getDeliveryCost()),2)));


                    tv = findViewById(R.id.orderNumber);
                    tv.setText(order_id);

                    tv = findViewById(R.id.Date);
                    String pattern = "MMMM dd, yyyy hh:mm a";
                    SimpleDateFormat simpleDateFormat =
                            new SimpleDateFormat(pattern, new Locale("en", "IN"));
                    tv.setText(simpleDateFormat.format(orderModal.getDate()));

                    tv = findViewById(R.id.orderState);
                    if (orderModal.getState() == 1) {
                        placeOrderBtn.setText("Cancel Order");
                        tv.setText(getString(R.string.orderState1));
                    } else if (orderModal.getState() == 2) {
                        placeOrderBtn.setText("Call Shop");
                        tv.setText(getString(R.string.orderState2));
                    } else if (orderModal.getState() == 3) {
                        placeOrderBtn.setText("Confirm Delivery");
                        tv.setText(getString(R.string.orderState3));
                    } else if (orderModal.getState() == 4) {
                        placeOrderBtn.setText("Order Delivered.");
                        tv.setText(getString(R.string.orderState4));
                    }
                    else if(orderModal.getState() == 0){
                        placeOrderBtn.setText("Order is Canceled");
                        tv.setText("Order Canceled");
                    }


                    baseView.setVisibility(View.VISIBLE);
                    loader.setVisibility(View.GONE);
                }
            });
        }
         else {

             editProfileBtn.setVisibility(View.VISIBLE);
             editProfileBtn.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     startActivity(new Intent(CartActivity.this, ProfileDetailActivity.class));
                 }
             });

             db = Room.databaseBuilder(getApplicationContext(),
                    AppDataBase.class, "clientAppDB").fallbackToDestructiveMigration().build();
            List<CartItem> categoryFilterList;
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

                adapter = new CartAdapter(categoryFilterList, -1, CartActivity.this);
                categoryList.setAdapter(adapter);

                for (CartItem item : categoryFilterList) {
                    amount += item.getAmount();
                }

                TextView tv = findViewById(R.id.itemTotal);
                tv.setText(MessageFormat.format("₹ {0}", CartAdapter.round(amount,2)));

                tv = findViewById(R.id.totalAmount);
                tv.setText(String.valueOf(CartAdapter.round((amount + deliveryCharge),2)));



            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            placeOrderBtn.setText(R.string.placeOrder);
            TextView orderState = findViewById(R.id.orderState);
            orderState.setText(R.string.orderInCart);
            baseView.setVisibility(View.VISIBLE);
            loader.setVisibility(View.GONE);
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
