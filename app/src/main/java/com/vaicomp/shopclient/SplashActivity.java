package com.vaicomp.shopclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaicomp.shopclient.db.AppDataBase;
import com.vaicomp.shopclient.db.CategoryFilter;
import com.vaicomp.shopclient.db.ShopItem;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);


        try {
            String vName = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView tv = findViewById(R.id.versionText);
            tv.setText(vName);

            if (!preferenceManager.getIsLoggedIn(getApplicationContext())) {
                SignInButton signInButton = findViewById(R.id.sign_in_button);
                TextView loader = findViewById(R.id.loader);
                loader.setVisibility(View.GONE);
                signInButton.setVisibility(View.VISIBLE);
                signInButton.setSize(SignInButton.SIZE_STANDARD);

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();


                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                mAuth = FirebaseAuth.getInstance();
                signInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, 200);
                    }
                });
            } else {
                SignInButton signInButton = findViewById(R.id.sign_in_button);
                signInButton.setVisibility(View.GONE);
                TextView loader = findViewById(R.id.loader);
                loader.setVisibility(View.VISIBLE);
                loadAllDataToLocalDB(SplashActivity.this);
            }


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                updateUI(null);
                            }
                        }
                    });
        } catch (ApiException e) {
            Log.w("SIGNIN OP ==>> ", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }


    void updateUI(FirebaseUser account) {
        if (account != null) {
            Context context = getApplicationContext();
            preferenceManager.setUID(context, account.getUid());
            preferenceManager.setDisplayName(context, account.getDisplayName());
            preferenceManager.setEmailId(context, account.getEmail());
            preferenceManager.setPhotoUrl(context, String.valueOf(account.getPhotoUrl()));
            startActivity(new Intent(SplashActivity.this, ProfileDetailActivity.class));
            finish();
        } else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            preferences.edit().clear().apply();
            if (!false) {
                Toasty.error(getApplicationContext(), "Sign-In Failed :(", Toasty.LENGTH_LONG).show();
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    static void loadAllDataToLocalDB(final Activity activity) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.i("TAG", "1");

        db.collection("shopItemState").document("d1ajtkwauTOe8z27xdH8").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final String itemState = String.valueOf(documentSnapshot.get("itemState"));
                final String bannerUrl = String.valueOf(documentSnapshot.get("bannerURL"));
                preferenceManager.setBannerURL(activity, bannerUrl);
                final String latestVersion = String.valueOf(documentSnapshot.get("ClientVersion"));
                final String updateType = String.valueOf(documentSnapshot.get("clientUpdateType"));

                try {
                    PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                    String version = pInfo.versionName;
                    if (version.equals(latestVersion)) {


                        if (!itemState.equals(preferenceManager.getItemState(activity))) {
                            proceedWithOutUpdate(activity, db, itemState);
                        } else {
                            activity.startActivity(new Intent(activity, CategoryGridActivity.class));
                            activity.finish();
                        }
                    } else {
                        Log.i("update", "YES");
                        if (updateType.toLowerCase().equals("force")) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                            alertDialog.setTitle("Update Available!");
                            alertDialog.setMessage("This is a mandatory update, which improves application security & overall performance.");
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    try {
                                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
                                    }
                                    alertDialog.dismiss();
                                    activity.finish();
                                }
                            });
                            alertDialog.show();
                        } else {
                            final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                            alertDialog.setTitle("Update Available!");
                            alertDialog.setMessage("This is a optional update, which improves application security & overall performance.");
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    try {
                                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
                                    }
                                    alertDialog.dismiss();
                                    activity.finish();
                                }
                            });
                            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    alertDialog.dismiss();
                                    proceedWithOutUpdate(activity, db, itemState);
                                }
                            });
                            alertDialog.show();
                        }

                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private static void proceedWithOutUpdate(final Activity activity, final FirebaseFirestore db, final String itemState) {
        db.collection("shopItems").whereGreaterThan("itemRate", 0).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list1 = queryDocumentSnapshots.getDocuments();
                Log.i("TAG", "2");
                if (list1.size() != 0) {
                    final AppDataBase local_db = Room.databaseBuilder(activity.getBaseContext(),
                            AppDataBase.class, "clientAppDB").fallbackToDestructiveMigration().build();

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            local_db.shopItemDao().nukeTable();
                            local_db.categoryFilterDao().nukeTable();
                            return null;
                        }
                    }.execute();

                    final ShopItem[] itemList = new ShopItem[list1.size()];
                    int itr = 0;
                    for (DocumentSnapshot ds : list1) {
                        ShopItem item = new ShopItem();

                        item.setItemId(ds.getId());
                        item.setItemName(String.valueOf(ds.get("itemName")));
                        item.setCategory(String.valueOf(ds.get("category")));
                        item.setImageUrl(String.valueOf(ds.get("photoUrl")));
                        item.setRate(Double.valueOf(String.valueOf(ds.get("itemRate"))));
                        item.setAmount((double) 0);
                        item.setQuantity(0);
                        Log.i("TAG", "loop");

                        itemList[itr] = item;
                        itr++;
                    }
                    Log.i("TAG", "3");

                    db.collection("category").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Log.i("TAG", "4");
                            List<DocumentSnapshot> list1 = queryDocumentSnapshots.getDocuments();
                            if (list1.size() != 0) {
                                final CategoryFilter[] categoryFilterList = new CategoryFilter[list1.size()];
                                int itr = 0;
                                for (DocumentSnapshot ds : list1) {
                                    categoryFilterList[itr] = new CategoryFilter(String.valueOf(ds.get("categoryName")), false);
                                    itr++;
                                }
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        local_db.categoryFilterDao().insertAll(categoryFilterList);
                                        local_db.shopItemDao().insertAll(itemList);
                                        activity.startActivity(new Intent(activity, CategoryGridActivity.class));
                                        preferenceManager.setItemState(activity, itemState);
                                        activity.finish();
                                        return null;
                                    }
                                }.execute();
                            }
                        }
                    });

                }
            }
        });
    }
}
