package com.water.alkaline.kengen.ui.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceDataStore;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.BuildConfig;
import com.water.alkaline.kengen.Encrypt.DecryptEncrypt;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.data.network.RetroClient;
import com.water.alkaline.kengen.databinding.ActivitySplashBinding;
import com.water.alkaline.kengen.databinding.DialogInternetBinding;
import com.water.alkaline.kengen.library.ViewAnimator.AnimationListener;
import com.water.alkaline.kengen.library.ViewAnimator.ViewAnimator;
import com.water.alkaline.kengen.model.main.MainResponse;
import com.water.alkaline.kengen.model.update.AdsInfo;
import com.water.alkaline.kengen.model.update.AppInfo;
import com.water.alkaline.kengen.model.update.UpdateResponse;
import com.water.alkaline.kengen.placements.BackInterAds;
import com.water.alkaline.kengen.placements.BannerAds;
import com.water.alkaline.kengen.placements.InterAds;
import com.water.alkaline.kengen.placements.InterSplashAds;
import com.water.alkaline.kengen.placements.LargeNativeAds;
import com.water.alkaline.kengen.placements.ListNativeAds;
import com.water.alkaline.kengen.placements.MainAds;
import com.water.alkaline.kengen.placements.MiniNativeAds;
import com.water.alkaline.kengen.placements.OpenSplashAds;
import com.water.alkaline.kengen.placements.OpenAds;
import com.water.alkaline.kengen.utils.Constant;
import com.water.alkaline.kengen.utils.MyService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity implements ShadowsocksConnection.Callback, OnPreferenceDataStoreChangeListener {

    public static SplashActivity activity;
    public AppViewModel viewModel;
    public Dialog dialog;
    ShadowsocksConnection shadowsocksConnection = new ShadowsocksConnection(true);
    ActivityResultLauncher<Void> launcher;
    ActivitySplashBinding binding;
    int REQUEST_PERMISSIONS = 200;
    boolean check = false;
    int VERSION = 0;

    public void setBG() {
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
    }

    public void dismiss_dialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public DialogInternetBinding network_dialog(String text) {
        dialog = new Dialog(this, R.style.NormalDialog);
        DialogInternetBinding binding = DialogInternetBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        binding.txtError.setText(text);
        return binding;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;

        PowerPreference.getDefaultFile().putBoolean(Constant.mIsLoaded, false);
        PowerPreference.getDefaultFile().putInt(Constant.mIsDuration, 1);
        PowerPreference.getDefaultFile().putBoolean(Constant.isRunning, true);

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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isMyServiceRunning(MyService.class))
                    startService(new Intent(SplashActivity.this, MyService.class));
            }
        }, 2000);

        ViewAnimator.animate(binding.ivLogo)
                .scale(0f, 1f)
                .andAnimate(binding.llText)
                .alpha(0f, 1f)
                .duration(1000)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        binding.progress.setVisibility(View.VISIBLE);
                        setBG();
                        startApp();
                    }
                }).start();


    }

    public void startApp() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MyApplication.getMain(SplashActivity.this).equalsIgnoreCase("")) {
                    network_dialog("Something Went Wrong\nPlease Try Again Later !")
                            .txtRetry.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dismiss_dialog();
                                }
                            });
                } else {
                    getToken();
                }
            }
        }, 1000);
    }

    public void getToken() {
        if (Constant.checkInternet(SplashActivity.this)) {
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(@NonNull String s) {
                    PowerPreference.getDefaultFile().putString(Constant.Token, s);
                    callAPI();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    PowerPreference.getDefaultFile().putString(Constant.Token, "123");
                    callAPI();
                }
            });
        } else {
            handler.sendEmptyMessage(998);
        }
    }

    public void callAPI() {
        if (Constant.checkInternet(SplashActivity.this)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Document doc = Jsoup.connect(MyApplication.getBase(SplashActivity.this)).get();
                        PowerPreference.getDefaultFile().putString(Constant.apiKey, doc.select("strong").get(0).text());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateAPI();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            handler.sendEmptyMessage(999);
        }
    }

    public void loadAds() {
        MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("DD54D1B666D3213A83E29EFFA7F3AED4")).build());
        PowerPreference.getDefaultFile().putBoolean(Constant.mIsLoaded, true);

        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", PowerPreference.getDefaultFile().getString(Constant.APPID, "ca-app-pub-3940256099942544~3347511713"));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("TAG", "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e("TAG", "Failed to load meta-data, NullPointer: " + e.getMessage());
        }

        MobileAds.initialize(SplashActivity.this);
        new MainAds().loadAds(this);
    }

    public void updateAPI() {

        if (Constant.checkInternet(SplashActivity.this)) {
            @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String token = PowerPreference.getDefaultFile().getString(Constant.Token, "");

            PackageManager manager = getPackageManager();
            PackageInfo info = null;

            try {
                info = manager.getPackageInfo(getPackageName(), 0);
                VERSION = info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Constant.showLog(e.toString());
                VERSION = BuildConfig.VERSION_CODE;
            }


            RetroClient.getInstance(this).getApi().updateApi(DecryptEncrypt.EncryptStr(SplashActivity.this, deviceId), DecryptEncrypt.EncryptStr(SplashActivity.this, token), DecryptEncrypt.EncryptStr(SplashActivity.this, getPackageName()), VERSION)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                final UpdateResponse updateResponse = new GsonBuilder().create().fromJson((DecryptEncrypt.DecryptStr(SplashActivity.this, response.body())), UpdateResponse.class);

                                if (updateResponse != null) {
                                    AdsInfo appData = updateResponse.getData().getAdsInfo().get(0);


                                    PowerPreference.getDefaultFile().putString(Constant.mToken, updateResponse.getMtoken());

                                    PowerPreference.getDefaultFile().putString(Constant.APPID, appData.getGoogleAppIdAds());

                                    PowerPreference.getDefaultFile().putString(Constant.BANNERID, appData.getGoogleBannerAds());
                                    PowerPreference.getDefaultFile().putString(Constant.INTERID, appData.getGoogleInterAds());
                                    PowerPreference.getDefaultFile().putString(Constant.NATIVEID, appData.getGoogleNativeAds());
                                    PowerPreference.getDefaultFile().putString(Constant.OPENAD, appData.getGoogleAppOpenAds());

                                    PowerPreference.getDefaultFile().putInt(Constant.AppOpen, appData.getAppOpen());

                                    PowerPreference.getDefaultFile().putBoolean(Constant.AdsOnOff, appData.getAdsOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.GoogleAdsOnOff, appData.getGoogleAdsOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.QurekaOnOff, appData.getQurekaOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.LoaderNativeOnOff, appData.getLoaderNativeOnOff());

                                    PowerPreference.getDefaultFile().putBoolean(Constant.GoogleSplashOpenAdsOnOff, appData.getGoogleSplashOpenAdsOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.GoogleExitSplashInterOnOff, appData.getGoogleExitSplashInterOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.GoogleAppOpenAdsOnOff, appData.getGoogleAppOpenAdsOnOff());


                                    PowerPreference.getDefaultFile().putInt(Constant.SERVER_INTERVAL_COUNT, appData.getIntervalCount());
                                    PowerPreference.getDefaultFile().putInt(Constant.APP_INTERVAL_COUNT, 0);

                                    PowerPreference.getDefaultFile().putInt(Constant.SERVER_BACK_COUNT, appData.getBackIntervalCount());
                                    PowerPreference.getDefaultFile().putInt(Constant.APP_BACK_COUNT, 0);

                                    PowerPreference.getDefaultFile().putBoolean(Constant.GoogleBannerOnOff, appData.getGoogleBannerOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.GoogleInterOnOff, appData.getGoogleInterOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.GoogleBackInterOnOff, appData.getGoogleBackInterOnOff());

                                    PowerPreference.getDefaultFile().putBoolean(Constant.GoogleMiniNativeOnOff, appData.getGoogleMiniNativeOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.GoogleLargeNativeOnOff, appData.getGoogleLargeNativeOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.GoogleListNativeOnOff, appData.getGoogleListNativeOnOff());

                                    PowerPreference.getDefaultFile().putInt(Constant.BannerAdWhichOne, appData.getBannerAdWhichOne());

                                    PowerPreference.getDefaultFile().putInt(Constant.ListNativeWhichOne, appData.getListNativeWhichOne());
                                    PowerPreference.getDefaultFile().putInt(Constant.ListNativeAfterCount, appData.getListNativeAfterCount());

                                    PowerPreference.getDefaultFile().putBoolean(Constant.QurekaIconOnOff, appData.getQurekaIconOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.QurekaBannerOnOff, appData.getQurekaBannerOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.QurekaInterOnOff, appData.getQurekaInterOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.QurekaBackInterOnOff, appData.getQurekaBackInterOnOff());

                                    PowerPreference.getDefaultFile().putBoolean(Constant.QurekaMiniNativeOnOff, appData.getQurekaMiniNativeOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.QurekaLargeNativeOnOff, appData.getQurekaLargeNativeOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.QurekaListNativeOnOff, appData.getQurekaListNativeOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.QurekaAppOpenOnOff, appData.getQurekaAppOpenOnOff());

                                    PowerPreference.getDefaultFile().putBoolean(Constant.ShowDialogBeforeAds, appData.getShowDialogBeforeAds());
                                    PowerPreference.getDefaultFile().putDouble(Constant.DialogTimeInSec, appData.getDialogTimeInSec());

                                    PowerPreference.getDefaultFile().putBoolean(Constant.VpnOnOff, appData.getVpnOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.VpnAuto, appData.getVpnAuto());
                                    PowerPreference.getDefaultFile().putString(Constant.VpnUrl, appData.getVpnUrl());

                                    PowerPreference.getDefaultFile().putInt(Constant.AD_CLICK_COUNT, appData.getAdsCloseCount());

                                    if (updateResponse.getFeedbacks() != null) {
                                        PowerPreference.getDefaultFile().putString(Constant.mFeeds, new Gson().toJson(updateResponse.getFeedbacks()));
                                    }

                                    AppInfo appInfo = updateResponse.getData().getAppInfo().get(0);
                                    PowerPreference.getDefaultFile().putString(Constant.mKeyId, appInfo.getApiKey());
                                    PowerPreference.getDefaultFile().putString(Constant.mNotice, appInfo.getAppNotice());

                                    PowerPreference.getDefaultFile().putString(Constant.notifyKey, appInfo.getNotifyKey());
                                    PowerPreference.getDefaultFile().putString(Constant.QUREKA_ADS, appInfo.getQureka());

                                    PowerPreference.getDefaultFile().putString(Constant.appShareMsg, appInfo.getAppShareMsg());
                                    PowerPreference.getDefaultFile().putString(Constant.vidShareMsg, appInfo.getVidShareMsg());

                                    if (!PowerPreference.getDefaultFile().getString(Constant.T_DATE, "not").equalsIgnoreCase(appInfo.getTodayDate())) {
                                        PowerPreference.getDefaultFile().putString(Constant.T_DATE, appInfo.getTodayDate());
                                        PowerPreference.getDefaultFile().putInt(Constant.APP_CLICK_COUNT, 0);
                                    } else {
                                        int clickCOunt2 = PowerPreference.getDefaultFile().getInt(Constant.APP_CLICK_COUNT, 0);
                                        if (clickCOunt2 >= PowerPreference.getDefaultFile().getInt(Constant.AD_CLICK_COUNT, 3)) {
                                            PowerPreference.getDefaultFile().putBoolean(Constant.GoogleAdsOnOff, false);
                                        }
                                    }

                                    if (!appInfo.getTitle().equals("")) {
                                        binding.txtName.setText(appInfo.getTitle());
                                        binding.txtName.setVisibility(View.VISIBLE);
                                    }
                                    if (!appInfo.getDescription().equals("")) {
                                        binding.txtDes.setText(appInfo.getDescription());
                                        binding.txtDes.setVisibility(View.VISIBLE);
                                    }
                                    if (!appInfo.getButtonName().equals("")) {
                                        binding.txtUpdate.setText(appInfo.getButtonName());
                                    }
                                    if (!appInfo.getButtonSkip().equals("")) {
                                        binding.txtSkip.setText(appInfo.getButtonSkip());
                                    }

                                    String flag = appInfo.getFlag();
                                    boolean flagCheck = true;
                                    if (flag.equals("NORMAL")) {

                                    } else if (flag.equals("SKIP")) {
                                        if (VERSION < Integer.parseInt(appInfo.getVersion())) {
                                            binding.cvUpdate.setVisibility(View.VISIBLE);
                                            binding.txtUpdate.setVisibility(View.VISIBLE);
                                            binding.txtSkip.setVisibility(View.VISIBLE);
                                            binding.cvSplash.setVisibility(View.GONE);
                                            flagCheck = false;
                                        } else {
                                            binding.cvUpdate.setVisibility(View.GONE);
                                            binding.cvSplash.setVisibility(View.VISIBLE);
                                            flagCheck = true;
                                        }
                                    } else if (flag.equals("MOVE")) {
                                        binding.cvUpdate.setVisibility(View.VISIBLE);
                                        binding.txtSkip.setVisibility(View.GONE);
                                        binding.txtUpdate.setVisibility(View.VISIBLE);
                                        binding.cvSplash.setVisibility(View.GONE);
                                        flagCheck = false;

                                    } else if (flag.equals("FORCE")) {
                                        if (VERSION < Integer.parseInt(appInfo.getVersion())) {
                                            binding.cvUpdate.setVisibility(View.VISIBLE);
                                            binding.txtSkip.setVisibility(View.GONE);
                                            binding.txtUpdate.setVisibility(View.VISIBLE);
                                            binding.cvSplash.setVisibility(View.GONE);
                                            flagCheck = false;
                                        } else {
                                            binding.cvUpdate.setVisibility(View.GONE);
                                            binding.cvSplash.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    binding.txtUpdate.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appInfo.getLink())));
                                        }
                                    });

                                    binding.txtSkip.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            binding.cvUpdate.setVisibility(View.GONE);
                                            binding.cvSplash.setVisibility(View.VISIBLE);
                                            mainAPI();
                                        }
                                    });

                                    if (flagCheck) {
                                        mainAPI();
                                    }

                                } else {
                                    handler.sendEmptyMessage(1000);
                                }
                            } catch (Exception e) {
                                Constant.showLog(e.toString());
                                e.printStackTrace();
                                Constant.showToast(SplashActivity.this, "Something went Wrong");
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Constant.showLog(t.getMessage());
                            handler.sendEmptyMessage(1000);
                        }
                    });
        } else {
            handler.sendEmptyMessage(1000);
        }
    }

    public void mainAPI() {
        if (Constant.checkInternet(SplashActivity.this)) {
            RetroClient.getInstance(this).getApi().dataApi(DecryptEncrypt.EncryptStr(SplashActivity.this, PowerPreference.getDefaultFile().getString(Constant.mToken, "123"))).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        final MainResponse mainResponse = new GsonBuilder().create().fromJson((DecryptEncrypt.DecryptStr(SplashActivity.this, response.body())), MainResponse.class);

                        viewModel.deleteAllCategory();
                        viewModel.deleteAllSubCategory();
                        viewModel.deleteAllBanner();
                        viewModel.deleteAllChannel();
                        viewModel.deleteAllPdf();

                        viewModel.insertCategory(mainResponse.getData().getCategorys());
                        viewModel.insertSubCategory(mainResponse.getData().getSubcategorys());
                        viewModel.insertChannel(mainResponse.getData().getChannels());
                        viewModel.insertPdf(mainResponse.getData().getPdfs());
                        viewModel.insertBanner(mainResponse.getData().getBanners());

                        checkVpnApp();

                    } catch (Exception e) {
                        Constant.showLog(e.toString());
                        e.printStackTrace();
                        Constant.showToast(SplashActivity.this, "Something went Wrong");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    handler.sendEmptyMessage(1001);
                }
            });
        } else {
            handler.sendEmptyMessage(1001);
        }
    }

    public Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (!SplashActivity.this.isFinishing()) {
                network_dialog(getResources().getString(R.string.error_internet)).txtRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss_dialog();
                        if (Constant.checkInternet(SplashActivity.this)) {
                            if (msg.what == 1000) {
                                updateAPI();
                            } else if (msg.what == 1001) {
                                mainAPI();
                            } else if (msg.what == 998) {
                                getToken();
                            } else {
                                callAPI();
                            }
                        } else dialog.show();
                    }
                });
            } else {
                Log.e("TAG", "Something went wrong");
            }
        }
    };

    public void checkVpnApp() {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.VpnOnOff, true) && PowerPreference.getDefaultFile().getBoolean(Constant.VpnAuto, true)) {
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

            launcher.launch(null);
        } else if (PowerPreference.getDefaultFile().getBoolean(Constant.VpnOnOff, true)) {
            startActivity(new Intent(SplashActivity.this, VpnActivity.class));
        } else {
            nextActivity();
        }
    }

    public void nextActivity() {
        loadAds();
        FirebaseMessaging.getInstance().subscribeToTopic(PowerPreference.getDefaultFile().getString(Constant.notifyKey, "kengen")).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleSplashOpenAdsOnOff, false)) {
                    if (PowerPreference.getDefaultFile().getInt(Constant.AppOpen, 1) == 1) {
                        new MainAds().showOpenAds(SplashActivity.this, new OpenSplashAds.OnAdClosedListener() {
                            @Override
                            public void onAdClosed() {
                                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                            }
                        });

                    } else if (PowerPreference.getDefaultFile().getInt(Constant.AppOpen, 1) == 2) {
                        new MainAds().showSplashInterAds(SplashActivity.this, new InterSplashAds.OnAdClosedListener() {
                            @Override
                            public void onAdClosed() {
                                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                            }
                        });
                    } else {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    }
                } else
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            }
        }, 4000);

    }

    private boolean isMyServiceRunning(Class<?> cls) {
        for (ActivityManager.RunningServiceInfo runningServiceInfo : ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(runningServiceInfo.service.getClassName())) {
                Log.e("ServiceStatus", "Running");
                return true;
            }
        }
        Log.e("ServiceStatus", "Not running");
        return false;
    }

    @Override
    public void stateChanged(@NonNull BaseService.State state, @Nullable String
            profileName, @Nullable String msg) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataStore.INSTANCE.getPublicStore().unregisterChangeListener(this);
        shadowsocksConnection.disconnect(this);
        activity = null;
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