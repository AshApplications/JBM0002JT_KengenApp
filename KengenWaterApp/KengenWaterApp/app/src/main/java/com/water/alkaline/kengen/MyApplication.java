package com.water.alkaline.kengen;

import android.app.Application;
import android.content.Context;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.firebase.FirebaseApp;
import com.google.gms.ads.CustomApplication;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.library.downloader.PRDownloader;

import java.util.ArrayList;
import java.util.Arrays;

public class MyApplication extends CustomApplication {

    public static ArrayList<Integer> arrayList = new ArrayList<>();

    private static MyApplication instance;

    static {
        System.loadLibrary("native-lib");
    }

    public AppViewModel viewModel;

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

    public static native String getBase(Context context);

    public static native String getMain(Context context);

    public static native String getSub(Context context);

    public static native String getKey1(Context context);

    public static native String getKey2(Context context);


    public AppViewModel getViewModel() {
        if (viewModel == null)
            viewModel = new AppViewModel(this);
        return viewModel;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        PRDownloader.initialize(getApplicationContext());
        FirebaseApp.initializeApp(getApplicationContext());
    }

}
