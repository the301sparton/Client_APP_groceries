package com.vaicomp.shopclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaicomp.shopclient.db.AppDataBase;
import com.vaicomp.shopclient.db.ShopItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class ProfileDetailActivity extends AppCompatActivity {

    Context context;
    SmoothProgressBar progress_horizontal;
    Boolean toInsert = true;
    String accountId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile_detail);
        context = getApplicationContext();

        final TextInputEditText displayName, emailId, phoneNum, address;
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        displayName = findViewById(R.id.displayName);
        emailId = findViewById(R.id.emailId);
        phoneNum = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.address);
        progress_horizontal = findViewById(R.id.progress_horizontal);


        displayName.setText(preferenceManager.getDisplayName(context));
        emailId.setText(preferenceManager.getEmailId(context));
        phoneNum.setText(preferenceManager.getPhoneNumber(context));
        address.setText(preferenceManager.getAdress(context));


        db.collection("users").whereEqualTo("UID",preferenceManager.getUID(context)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size() > 0) {
                    DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                    toInsert = false;
                    accountId = snapshot.getId();
                    phoneNum.setText(String.valueOf(snapshot.get("phoneNumber")));
                    address.setText(String.valueOf(snapshot.get("address")));
                }

            }
        });

        Button saveProfile = findViewById(R.id.saveProfile);

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //preferenceManager.setUID(context, account.getId());
                preferenceManager.setDisplayName(context,displayName.getText().toString());
                preferenceManager.setEmailId(context, emailId.getText().toString());
                preferenceManager.setAddress(context, address.getText().toString());
                preferenceManager.setPhoneNumber(context, phoneNum.getText().toString());

                progress_horizontal.setVisibility(View.VISIBLE);
                final Map<String, Object> user = new HashMap<>();
                user.put("UID", preferenceManager.getUID(context));
                user.put("displayName", displayName.getText().toString());
                user.put("emailId", emailId.getText().toString());
                user.put("phoneNumber", phoneNum.getText().toString());
                user.put("address",address.getText().toString());
                user.put("photoUrl", preferenceManager.getPhotoUrl(context));

                if(toInsert){
                    db.collection("users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            preferenceManager.setIsLoggedIn(context, true);
                            Toasty.success(getApplicationContext(), "Profile Details Saved!", Toasty.LENGTH_SHORT).show();
                            SplashActivity.loadAllDataToLocalDB(ProfileDetailActivity.this);
                        }
                    });
                }
                else {
                    db.collection("users").document(accountId).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            preferenceManager.setIsLoggedIn(context, true);
                            Toasty.success(getApplicationContext(), "Profile Details Saved!", Toasty.LENGTH_SHORT).show();
                            SplashActivity.loadAllDataToLocalDB(ProfileDetailActivity.this);
                        }
                    });
                }
            }
        });
    }



}


