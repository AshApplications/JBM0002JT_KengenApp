package com.water.alkaline.kengen;

import android.app.Application;
import android.content.Context;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.github.shadowsocks.Core;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.library.downloader.PRDownloader;
import com.water.alkaline.kengen.ui.activity.VpnActivity;

import java.util.ArrayList;

public class MyApplication extends Application {

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

    public static native String getIcon(Context context);

    public static native String getBanner(Context context);

    public static native String getInter(Context context);

    public static native String getRound(Context context);

    public static native String getFlag(Context context);

    public AppViewModel getViewModel() {
        if (viewModel == null)
            viewModel = new AppViewModel(this);
        return viewModel;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Core.INSTANCE.init(this, VpnActivity.class);
        PRDownloader.initialize(getApplicationContext());
    }

}
