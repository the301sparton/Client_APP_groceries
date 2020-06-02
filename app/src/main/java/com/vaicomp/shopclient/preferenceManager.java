package com.vaicomp.shopclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class preferenceManager {

    static void setIsLoggedIn(Context context, boolean isLoggedIn){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply();
    }

    static boolean getIsLoggedIn(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("isLoggedIn",false);
    }

    static void setUID(Context context, String secret){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString("UID", secret).apply();
    }

    public static String getUID(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("UID","");
    }

    static void setItemState(Context context, String itemState){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString("itemState", itemState).apply();
    }

    public static String getItemState(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("itemState","");
    }


    static void setBannerURL(Context context, String categoryState){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString("bannerURL", categoryState).apply();
    }

    public static String getBannerUrl(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("bannerURL","");
    }


    static void setDisplayName(Context context, String secret){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString("displayName", secret).apply();
    }

    static String getDisplayName(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("displayName","");
    }

    static void setEmailId(Context context, String secret){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString("emailId", secret).apply();
    }

    static String getEmailId(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("emailId","");
    }

    static void setPhotoUrl(Context context, String secret){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString("photoUrl", secret).apply();
    }

    static String getPhotoUrl(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("photoUrl","");
    }

    static void setPhoneNumber(Context context, String secret){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString("PhoneNumber", secret).apply();
    }

    static String getPhoneNumber(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("PhoneNumber","");
    }

    static void setAddress(Context context, String secret){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString("address", secret).apply();
    }

    static String getAdress(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("address","");
    }
}
