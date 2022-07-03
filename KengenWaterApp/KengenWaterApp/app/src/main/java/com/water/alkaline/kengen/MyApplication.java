package com.water.alkaline.kengen;

import android.app.Application;
import android.content.Context;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.library.downloader.PRDownloader;

import java.util.ArrayList;

public class MyApplication extends Application {

    public static ArrayList<Integer> arrayList = new ArrayList<>();

    private static MyApplication instance;
    public AppViewModel viewModel;

    static {
        System.loadLibrary("protected");
    }

    public static MyApplication getInstance() {
        if (instance == null)
            instance = new MyApplication();
        return instance;
    }

    public AppViewModel getViewModel() {
        if (viewModel == null)
            viewModel = new AppViewModel(this);
        return viewModel;
    }

    public static CircularProgressDrawable getPlaceHolder() {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(getInstance());
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
        return circularProgressDrawable;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        PRDownloader.initialize(getApplicationContext());
    }

    public static native String getMain(Context context);

    public static native String getSub(Context context);

    public static native String getDecKey1(Context context);

    public static native String getDecKey2(Context context);

    public static native String getEncKey1(Context context);

    public static native String getEncKey2(Context context);
}
