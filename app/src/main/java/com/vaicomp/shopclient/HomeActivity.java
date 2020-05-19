package com.vaicomp.shopclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaicomp.shopclient.Adapters.CartAdapter;
import com.vaicomp.shopclient.Adapters.ItemAdapter;
import com.vaicomp.shopclient.db.AppDataBase;
import com.vaicomp.shopclient.db.CartItem;
import com.vaicomp.shopclient.ui.home.HomeFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        LinearLayout viewCart = toolbar.findViewById(R.id.viewCart);
        viewCart.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                final AppDataBase db = Room.databaseBuilder(getApplicationContext(),
                        AppDataBase.class, "clientAppDB").fallbackToDestructiveMigration().build();
                List<CartItem> categoryFilterList = new ArrayList<>();
                try {
                    categoryFilterList = new AsyncTask<Void, Void, List<CartItem>>() {
                        @Override
                        protected List<CartItem> doInBackground(Void... voids) {
                            return db.cartItemDao().getAll();
                        }
                    }.execute().get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                if(categoryFilterList.size() > 0){
                    startActivity(new Intent(HomeActivity.this, CartActivity.class)
                            .putExtra("ORDER_ID", "NA"));
                }
                else{
                    Toasty.warning(getApplicationContext(), "No Items In Cart",Toasty.LENGTH_SHORT).show();
                }
            }
        });

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logOut){
            LogOut(HomeActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    public static void LogOut(final Activity context){
        new Handler().postDelayed(new Runnable() {

            @Override
            @SuppressLint("StaticFieldLeak")
            public void run() {
                // This method will be executed once the timer is over
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                preferences.edit().clear().apply();
                context.startActivity(new Intent(context, SplashActivity.class));
                context.finish();
            }
        }, 500);

    }

}
