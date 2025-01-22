package com.google.gms.ads;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gms.ads.model.AdModel;
import com.google.gson.Gson;
import com.preference.PowerPreference;

public class MyApp extends Application {

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
        return new Gson().fromJson(PowerPreference.getDefaultFile().getString("data", new Gson().toJson(new AdModel())), AdModel.class);
    }

    public static void setAdModel(AdModel adModel) {
        PowerPreference.getDefaultFile().putString("data",new Gson().toJson(adModel));
    }

    public void showAdIfAvailable(@NonNull Activity activity, @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        AppOpenManager.showAdIfSplashAvailable(activity, onShowAdCompleteListener);
    }

    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }

}
