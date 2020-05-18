package com.vaicomp.shopclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaicomp.shopclient.Adapters.ItemAdapter;
import com.vaicomp.shopclient.db.AppDataBase;
import com.vaicomp.shopclient.db.ShopItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);


        try {
            String vName = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView tv = findViewById(R.id.versionText);
            tv.setText(vName);

            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (!preferenceManager.getIsLoggedIn(getApplicationContext())) {
                SignInButton signInButton = findViewById(R.id.sign_in_button);
                signInButton.setVisibility(View.VISIBLE);
                signInButton.setSize(SignInButton.SIZE_STANDARD);

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestProfile()
                        .build();

                final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

                signInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, 200);
                    }
                });
            }
            else{
                SignInButton signInButton = findViewById(R.id.sign_in_button);
                signInButton.setVisibility(View.GONE);
                loadAllDataToLocalDB(SplashActivity.this);
            }


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void loadAllDataToLocalDB(final Activity activity) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("shopItems").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list1 = queryDocumentSnapshots.getDocuments();
                if(list1.size() != 0){
                    final AppDataBase local_db = Room.databaseBuilder(getApplicationContext(),
                            AppDataBase.class, "clientAppDB").fallbackToDestructiveMigration().build();

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            local_db.shopItemDao().nukeTable();
                            return null;
                        }
                    }.execute();

                    final ShopItem[] itemList = new ShopItem[list1.size()];
                    int itr = 0;
                    for(DocumentSnapshot ds : list1) {
                        ShopItem item = new ShopItem();

                        item.setItemId(ds.getId());
                        item.setItemName(String.valueOf(ds.get("itemName")));
                        item.setCategory(String.valueOf(ds.get("category")));
                        item.setImageUrl(String.valueOf(ds.get("photoUrl")));
                        item.setRate(Double.valueOf(String.valueOf(ds.get("itemRate"))));
                        item.setAmount((double) 0);
                        item.setQuantity(0);

                        itemList[itr] = item;
                        itr++;
                    }
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            local_db.shopItemDao().insertAll(itemList);
                            return null;
                        }
                    }.execute();

                    startActivity(new Intent(activity, HomeActivity.class));
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            Log.w("SIGNIN OP ==>> ", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }


    void updateUI(GoogleSignInAccount account){
        if(account != null){
            Context context = getApplicationContext();
            preferenceManager.setUID(context, account.getId());
            preferenceManager.setDisplayName(context, account.getDisplayName());
            preferenceManager.setEmailId(context, account.getEmail());
            preferenceManager.setPhotoUrl(context, String.valueOf(account.getPhotoUrl()));
            startActivity(new Intent(SplashActivity.this, ProfileDetailActivity.class));
            finish();
        }
        else{
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            preferences.edit().clear().apply();
            if(!false) {
                Toasty.error(getApplicationContext(), "Sign-In Failed :(", Toasty.LENGTH_LONG).show();
            }
        }
    }
}
