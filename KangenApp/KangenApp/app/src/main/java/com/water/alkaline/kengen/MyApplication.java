package com.water.alkaline.kengen;

import android.app.Application;
import android.os.StrictMode;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.library.downloader.PRDownloader;
import com.water.alkaline.kengen.model.SaveEntity;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    public static ArrayList<Integer> arrayList = new ArrayList<>();

    static {
        System.loadLibrary("native-lib");
    }

    private static MyApplication instance;
    public AppViewModel viewModel;

    public static MyApplication getInstance() {
        if (instance == null)
            instance = new MyApplication();
        return instance;
    }

    public AppViewModel getViewModel() {
        if (viewModel == null)
            viewModel = new AppViewModel(MyApplication.getInstance());
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
}
