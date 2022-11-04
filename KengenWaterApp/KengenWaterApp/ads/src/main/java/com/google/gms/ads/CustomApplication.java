package com.google.gms.ads;

import android.app.Application;
import android.content.Context;

public class CustomApplication extends Application {
    public static final String TAG = CustomApplication.class
            .getSimpleName();

    public static CustomApplication mInstance;
    public static Context mActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mActivity = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mActivity = base;
    }

    public static synchronized CustomApplication getInstance() {
        return mInstance;
    }

    public static synchronized Context getContext() {
        return getInstance().getApplicationContext();
    }

}
