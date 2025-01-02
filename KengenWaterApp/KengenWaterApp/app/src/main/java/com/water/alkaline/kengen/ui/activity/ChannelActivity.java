package com.water.alkaline.kengen.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;

import com.google.gms.ads.AdLoader;
import com.google.gms.ads.MyApp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.water.alkaline.kengen.BuildConfig;
import com.water.alkaline.kengen.Encrypt.DecryptEncrypt;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.data.network.RetroClient;

import com.water.alkaline.kengen.databinding.ActivityChannelBinding;
import com.water.alkaline.kengen.model.ErrorReponse;
import com.water.alkaline.kengen.model.SaveEntity;
import com.water.alkaline.kengen.model.channel.ChannelResponse;
import com.water.alkaline.kengen.model.channel.PlaylistResponse;
import com.water.alkaline.kengen.model.main.Channel;
import com.water.alkaline.kengen.model.update.AppInfo;
import com.water.alkaline.kengen.model.update.UpdateResponse;
import com.water.alkaline.kengen.ui.adapter.ChannelAdapter;
import com.water.alkaline.kengen.ui.adapter.VideosAdapter;
import com.water.alkaline.kengen.ui.fragment.ChannelFragment;
import com.water.alkaline.kengen.ui.listener.OnChannelListener;
import com.water.alkaline.kengen.ui.listener.OnLoadMoreListener;
import com.water.alkaline.kengen.ui.listener.OnVideoListener;
import com.water.alkaline.kengen.utils.Constant;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.utils.uiController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChannelActivity extends AppCompatActivity {

    ActivityChannelBinding binding;
    String catId;
    boolean isChannel;
    public AppViewModel viewModel;


    public void setBG() {
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        binding.includedToolbar.ivBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        uiController.onBackPressed(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApp.getAdModel().getAdsOnOff().equalsIgnoreCase("Yes")) {
            if (binding.includedAd.flAd.getChildCount() <= 0) {
                AdLoader.getInstance().showUniversalAd(this, binding.includedAd, false);
            }
        } else {
            binding.includedAd.cvAdMain.setVisibility(View.GONE);
            binding.includedAd.flAd.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getIntent() != null && getIntent().getExtras() != null) {
            catId = getIntent().getExtras().getString("catId", "894398");
            isChannel = getIntent().getExtras().getBoolean("isChannel", false);
        }
        setBG();
        if (!catId.equalsIgnoreCase("894398")) {
            startApp();
        }
    }

    public void startApp() {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, ChannelFragment.newInstance(this, catId, getIntent().hasExtra("isChannel") ? "Video" : "Channel", isChannel));
        transaction.addToBackStack(null);
        transaction.commit();
    }
}