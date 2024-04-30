package com.google.gms.ads;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gms.ads.model.AdModel;

public class MyApp extends Application {


    private static AdModel adModel = new AdModel();

    public static MyApp application;
    public static Context mActivity;
    private AppOpenManager AppOpenManager;

    public static String className = "";
    public static boolean isShowAds = true;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        mActivity = this;
        AppOpenManager = new AppOpenManager();
    }

    public static synchronized MyApp getInstance() {
        MyApp MyApp;
        synchronized (MyApp.class) {
            MyApp = application;
        }
        return MyApp;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mActivity = base;
    }


    public static synchronized Context getContext() {
        return getInstance().getApplicationContext();
    }

    public static AdModel getAdModel() {
        if (adModel == null) {
            adModel = new AdModel();
        }
        return adModel;
    }

    public static void setAdModel(AdModel adModel) {
        MyApp.adModel = adModel;
    }

    public void showAdIfAvailable(@NonNull Activity activity, @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        AppOpenManager.showAdIfSplashAvailable(activity, onShowAdCompleteListener);
    }

    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }

}
