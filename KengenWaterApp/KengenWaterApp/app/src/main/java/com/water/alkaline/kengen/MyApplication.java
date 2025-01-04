package com.water.alkaline.kengen;

import android.app.Application;
import android.content.Context;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.firebase.FirebaseApp;
import com.google.gms.ads.MyApp;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.library.downloader.PRDownloader;

import java.util.ArrayList;
import java.util.Arrays;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MyApplication extends MyApp {

    public static ArrayList<Integer> arrayList = new ArrayList<>();

    private static MyApplication instance;

    static {
        System.loadLibrary("native-lib");
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    public static CircularProgressDrawable getPlaceHolder() {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(getInstance());
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
        return circularProgressDrawable;
    }


    public static native String getSub(Context context);
    public static native String getKey1(Context context);
    public static native String getKey2(Context context);
    public static native String updateApi(Context context, String text1, String text2, String text3, String text4, String text5);
    public static native String sendFeedApi(Context context, String text1, String text2, String text3, String text4);


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        PRDownloader.initialize(getApplicationContext());
        FirebaseApp.initializeApp(getApplicationContext());
    }

}
