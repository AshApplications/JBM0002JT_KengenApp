package com.water.alkaline.kengen.ui.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceDataStore;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.shadowsocks.Core;
import com.github.shadowsocks.aidl.IShadowsocksService;
import com.github.shadowsocks.aidl.ShadowsocksConnection;
import com.github.shadowsocks.aidl.TrafficStats;
import com.github.shadowsocks.bg.BaseService;
import com.github.shadowsocks.database.Profile;
import com.github.shadowsocks.database.ProfileManager;
import com.github.shadowsocks.preference.DataStore;
import com.github.shadowsocks.preference.OnPreferenceDataStoreChangeListener;
import com.github.shadowsocks.utils.Key;
import com.github.shadowsocks.utils.StartService;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.databinding.ActivityVpnBinding;
import com.water.alkaline.kengen.placements.BackInterAds;
import com.water.alkaline.kengen.placements.BannerAds;
import com.water.alkaline.kengen.placements.InterAds;
import com.water.alkaline.kengen.placements.LargeNativeAds;
import com.water.alkaline.kengen.placements.ListNativeAds;
import com.water.alkaline.kengen.placements.MiniNativeAds;
import com.water.alkaline.kengen.placements.NewOpenAds;
import com.water.alkaline.kengen.placements.OpenAds;
import com.water.alkaline.kengen.utils.Constant;

import java.util.Arrays;

import timber.log.Timber;

public class VpnActivity extends AppCompatActivity implements ShadowsocksConnection.Callback, OnPreferenceDataStoreChangeListener {

    ActivityVpnBinding binding;
    ShadowsocksConnection shadowsocksConnection = new ShadowsocksConnection(true);
    ActivityResultLauncher<Void> launcher;
    BaseService.State statee = BaseService.State.Idle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVpnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        launcher = registerForActivityResult(
                new StartService(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result) {
                            nextActivity();
                        }
                    }
                });

        Glide.with(this).load(MyApplication.getFlag(this)).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.flag);

        try {

            String url = PowerPreference.getDefaultFile().getString(Constant.VpnUrl, "123");
            Profile profile = null;

            if (ProfileManager.INSTANCE.getAllProfiles() != null && ProfileManager.INSTANCE.getAllProfiles().size() > 0) {
                profile = ProfileManager.INSTANCE.getAllProfiles().get(0);
            } else {
                profile = new Profile();
                ProfileManager.INSTANCE.createProfile(profile);
            }
            profile.setHost(url.split("@")[1].split(":")[0]);
            profile.setMethod(url.split("@")[0].split(":")[0]);
            profile.setRemotePort(Integer.parseInt(url.split("@")[1].split(":")[1]));
            profile.setPassword(url.split("@")[0].split(":")[1]);
            ProfileManager.INSTANCE.updateProfile(profile);
            Core.INSTANCE.switchProfile(profile.getId());

        } catch (Exception e) {
            Timber.tag("TAG").e(e.toString());
            e.printStackTrace();
        }

        shadowsocksConnection.connect(this, this);
        DataStore.INSTANCE.getPublicStore().registerChangeListener(this);

        binding.vcontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextActivity();
            }
        });

        binding.btnConnect1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statee == BaseService.State.Connected) {
                    Core.INSTANCE.stopService();
                } else {
                    binding.layLoader.setVisibility(View.VISIBLE);
                    launcher.launch(null);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        if (this.statee == BaseService.State.Connected) {
            binding.btnConnect1.setImageResource(R.drawable.vp_connected);
            binding.vpnStatus.setText("Connected");
            binding.vpnStatus.setTextColor(ContextCompat.getColor(this, R.color.connect_vpn));
        } else {
            binding.btnConnect1.setImageResource(R.drawable.vp_connectedn);
            binding.vpnStatus.setText("Not Connected");
            binding.vpnStatus.setTextColor(ContextCompat.getColor(this, R.color.teal_600));
        }
    }

    @Override
    public void onBackPressed() {
        Constant.showRateDialog(this, false);
    }

    @Override
    public void stateChanged(@NonNull BaseService.State state, @Nullable String profileName, @Nullable String msg) {
        this.statee = state;
        updateUI();
        if (state.equals(BaseService.State.Connected)) {
            nextActivity();
        }
    }

    @Override
    public void trafficUpdated(long profileId, @NonNull TrafficStats stats) {
    }

    @Override
    public void trafficPersisted(long profileId) {
    }


    @Override
    public void onServiceConnected(@NonNull IShadowsocksService service) {
        try {
            if (service.getState() == 2) {
                statee = BaseService.State.Connected;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateUI();
    }

    @Override
    public void onServiceDisconnected() {
    }

    @Override
    public void onBinderDied() {
        shadowsocksConnection.disconnect(this);
        shadowsocksConnection.connect(this, this);
    }

    @Override
    public void onPreferenceDataStoreChanged(@NonNull PreferenceDataStore
                                                     store, @NonNull String key) {
        if (key.equalsIgnoreCase(Key.serviceMode)) {
            shadowsocksConnection.disconnect(this);
            shadowsocksConnection.connect(this, this);
        }
    }


    public void loadAds() {
        MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("6E27EC40561D1AD0FFC3A47FF91C5D1F")).build());
        PowerPreference.getDefaultFile().putBoolean(Constant.mIsLoaded, true);

        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", PowerPreference.getDefaultFile().getString(Constant.APPID, "ca-app-pub-3940256099942544~3347511713"));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("TAG", "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e("TAG", "Failed to load meta-data, NullPointer: " + e.getMessage());
        }

        MobileAds.initialize(VpnActivity.this);

        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, false)) {
            new BackInterAds().loadInterAds(VpnActivity.this);
            new BannerAds().loadBannerAds(VpnActivity.this);
            new InterAds().loadInterAds(VpnActivity.this);
            new LargeNativeAds().loadNativeAds(VpnActivity.this);
            new ListNativeAds().loadNativeAds(VpnActivity.this);
            new MiniNativeAds().loadNativeAds(VpnActivity.this);
            new OpenAds().loadOpenAd();
            new NewOpenAds().loadOpenAd(VpnActivity.this);
        }
    }


    public void nextActivity() {
        if (!PowerPreference.getDefaultFile().getBoolean(Constant.mIsLoaded, false)) {
            loadAds();
        }
        binding.layLoader.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleSplashOpenAdsOnOff, false)) {

                    if (PowerPreference.getDefaultFile().getInt(Constant.AppOpen, 1) == 1) {
                        new InterAds().watchAds(VpnActivity.this, new InterAds.OnAdClosedListener() {
                            @Override
                            public void onAdClosed() {
                                startActivity(new Intent(VpnActivity.this, HomeActivity.class));
                            }
                        });
                    } else if (PowerPreference.getDefaultFile().getInt(Constant.AppOpen, 1) == 2) {
                        new NewOpenAds().showOpenAd(VpnActivity.this, new NewOpenAds.OnAdClosedListener() {
                            @Override
                            public void onAdClosed() {
                                startActivity(new Intent(VpnActivity.this, HomeActivity.class));
                            }
                        });
                    } else {
                        startActivity(new Intent(VpnActivity.this, HomeActivity.class));
                    }
                } else
                    startActivity(new Intent(VpnActivity.this, HomeActivity.class));
            }
        }, 4000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataStore.INSTANCE.getPublicStore().unregisterChangeListener(this);
        shadowsocksConnection.disconnect(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        shadowsocksConnection.setBandwidthTimeout(500);
    }

    @Override
    protected void onStop() {
        shadowsocksConnection.setBandwidthTimeout(0);
        super.onStop();
    }
}