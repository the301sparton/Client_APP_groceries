package com.vaicomp.shopclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

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

    private void loadAllDataToLocalDB(Activity activity) {

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
