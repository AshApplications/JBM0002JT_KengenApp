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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.onesignal.OneSignal;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.BuildConfig;
import com.water.alkaline.kengen.Encrypt.DecryptEncrypt;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.google.gms.ads.AdUtils;
import com.google.gms.ads.InterAds;
import com.google.gms.ads.MainAds;
import com.google.gms.ads.OpenAds;
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
import com.water.alkaline.kengen.utils.Constant;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    public static SplashActivity activity;
    public AppViewModel viewModel;
    public Dialog dialog;

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
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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

        PowerPreference.getDefaultFile().putString(AdUtils.activitySplash, SplashActivity.class.getName());

        PowerPreference.getDefaultFile().putBoolean(Constant.mIsLoaded, false);
        PowerPreference.getDefaultFile().putInt(Constant.mIsDuration, 1);
        PowerPreference.getDefaultFile().putBoolean(Constant.isRunning, true);

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
                if (Constant.isVpnConnected()) {
                    network_dialog("VPN is Connected Please Turn it Off & Try Again !")
                            .txtRetry.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dismiss_dialog();
                                    startApp();
                                }
                            });
                } else if (MyApplication.getSub(SplashActivity.this).equalsIgnoreCase("")) {
                    network_dialog("Something Went Wrong\nPlease Try Again Later !")
                            .txtRetry.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dismiss_dialog();
                                }
                            });
                } else {
                    callAPI();
                }
            }
        }, 1000);
    }

    public void callAPI() {
        if (Constant.checkInternet(SplashActivity.this)) {
            FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600)
                    .build();
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
            mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
            mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(@NonNull Task<Boolean> task) {
                    PowerPreference.getDefaultFile().putString(Constant.apiKey, mFirebaseRemoteConfig.getValue("url").asString());
                    updateAPI();
                }
            });
        } else {
            handler.sendEmptyMessage(999);
        }
    }

    public void loadAds() {
        PowerPreference.getDefaultFile().putBoolean(Constant.mIsLoaded, true);
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", PowerPreference.getDefaultFile().getString(AdUtils.APPID, "ca-app-pub-3940256099942544~3347511713"));
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
            String token = PowerPreference.getDefaultFile().getString(Constant.Token, "123abc");

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

            RetroClient.getInstance(this).getApi().updateApi(DecryptEncrypt.EncryptStr(this, MyApplication.updateApi(this, deviceId, token, getPackageName(), String.valueOf(VERSION), "")))
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                final UpdateResponse updateResponse = new GsonBuilder().create().fromJson((DecryptEncrypt.DecryptStr(SplashActivity.this, response.body().string())), UpdateResponse.class);

                                if (updateResponse != null) {

                                    AdsInfo appData = updateResponse.getData().getAdsInfo().get(0);

                                    PowerPreference.getDefaultFile().putString(Constant.mToken, updateResponse.getMtoken());

                                    PowerPreference.getDefaultFile().putString(AdUtils.APPID, appData.getGoogleAppIdAds());

                                    PowerPreference.getDefaultFile().putString(AdUtils.BANNERID, appData.getGoogleBannerAds());
                                    PowerPreference.getDefaultFile().putString(AdUtils.INTERID, appData.getGoogleInterAds());
                                    PowerPreference.getDefaultFile().putString(AdUtils.NATIVEID, appData.getGoogleNativeAds());
                                    PowerPreference.getDefaultFile().putString(AdUtils.OPENAD, appData.getGoogleAppOpenAds());

                                    PowerPreference.getDefaultFile().putInt(AdUtils.AppOpenTime, appData.getAppOpenTime());

                                    PowerPreference.getDefaultFile().putBoolean(AdUtils.AdsOnOff, appData.getAdsOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(AdUtils.GoogleAdsOnOff, appData.getGoogleAdsOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(AdUtils.QurekaOnOff, appData.getQurekaOnOff());

                                    PowerPreference.getDefaultFile().putBoolean(AdUtils.LoaderNativeOnOff, appData.getLoaderNativeOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(AdUtils.ExitDialogNativeOnOff, appData.getExitDialogNativeOnOff());

                                    PowerPreference.getDefaultFile().putInt(AdUtils.SERVER_INTERVAL_COUNT, appData.getInterIntervalCount());
                                    PowerPreference.getDefaultFile().putInt(AdUtils.APP_INTERVAL_COUNT, 0);

                                    PowerPreference.getDefaultFile().putInt(AdUtils.SERVER_BACK_COUNT, appData.getBackInterIntervalCount());
                                    PowerPreference.getDefaultFile().putInt(AdUtils.APP_BACK_COUNT, 0);

                                    PowerPreference.getDefaultFile().putInt(AdUtils.WhichOneSplashAppOpen, appData.getWhichOneSplashAppOpen());
                                    PowerPreference.getDefaultFile().putInt(AdUtils.WhichOneBannerNative, appData.getWhichOneBannerNative());
                                    PowerPreference.getDefaultFile().putInt(AdUtils.WhichOneAllNative, appData.getWhichOneAllNative());
                                    PowerPreference.getDefaultFile().putInt(AdUtils.WhichOneListNative, appData.getWhichOneListNative());
                                    PowerPreference.getDefaultFile().putInt(AdUtils.ListNativeAfterCount, appData.getListNativeAfterCount());

                                    PowerPreference.getDefaultFile().putBoolean(AdUtils.ShowDialogBeforeAds, appData.getShowDialogBeforeAds());
                                    PowerPreference.getDefaultFile().putDouble(AdUtils.DialogTimeInSec, appData.getDialogTimeInSec());

                                    PowerPreference.getDefaultFile().putInt(AdUtils.AD_CLICK_COUNT, appData.getAdsCloseCount());

                                    if (updateResponse.getFeedbacks() != null) {
                                        PowerPreference.getDefaultFile().putString(Constant.mFeeds, new Gson().toJson(updateResponse.getFeedbacks()));
                                    }

                                    AppInfo appInfo = updateResponse.getData().getAppInfo().get(0);
                                    PowerPreference.getDefaultFile().putString(Constant.mKeyId, appInfo.getApiKey());
                                    PowerPreference.getDefaultFile().putString(Constant.mNotice, appInfo.getAppNotice());

                                    PowerPreference.getDefaultFile().putString(Constant.notifyKey, appInfo.getNotifyKey());
                                    PowerPreference.getDefaultFile().putString(AdUtils.QUREKA_ADS, appInfo.getQureka());

                                    PowerPreference.getDefaultFile().putString(Constant.appShareMsg, appInfo.getAppShareMsg());
                                    PowerPreference.getDefaultFile().putString(Constant.vidShareMsg, appInfo.getVidShareMsg());

                                    if (!PowerPreference.getDefaultFile().getString(Constant.T_DATE, "not").equalsIgnoreCase(appInfo.getTodayDate())) {
                                        PowerPreference.getDefaultFile().putString(Constant.T_DATE, appInfo.getTodayDate());
                                        PowerPreference.getDefaultFile().putInt(AdUtils.APP_CLICK_COUNT, 0);
                                    } else {
                                        int clickCOunt2 = PowerPreference.getDefaultFile().getInt(AdUtils.APP_CLICK_COUNT, 0);
                                        if (clickCOunt2 >= PowerPreference.getDefaultFile().getInt(AdUtils.AD_CLICK_COUNT, 3)) {
                                            PowerPreference.getDefaultFile().putBoolean(AdUtils.GoogleAdsOnOff, false);
                                        }
                                    }

                                    loadAds();

                                    OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
                                    OneSignal.initWithContext(MyApplication.getInstance());
                                    OneSignal.setAppId(appInfo.getOneSignalAppId());
                                    OneSignal.sendTag("deviceId", deviceId);

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
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
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
            RetroClient.getInstance(this).getApi().dataApi(DecryptEncrypt.EncryptStr(SplashActivity.this, MyApplication.updateApi(this,"", PowerPreference.getDefaultFile().getString(Constant.mToken, "123"),"","",""))).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        final MainResponse mainResponse = new GsonBuilder().create().fromJson((DecryptEncrypt.DecryptStr(SplashActivity.this, response.body().string())), MainResponse.class);

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

                        nextActivity();

                    } catch (Exception e) {
                        Constant.showLog(e.toString());
                        e.printStackTrace();
                        Constant.showToast(SplashActivity.this, "Something went Wrong");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    handler.sendEmptyMessage(1001);
                }
            });
        } else {
            handler.sendEmptyMessage(1001);
        }
    }

    public void nextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneSplashAppOpen, 0) == 1) {
                    new MainAds().showSplashInterAds(SplashActivity.this, new InterAds.OnAdClosedListener() {
                        @Override
                        public void onAdClosed() {
                            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        }
                    });
                } else if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneSplashAppOpen, 0) == 2) {
                    new MainAds().showOpenAds(SplashActivity.this, new OpenAds.OnAdClosedListener() {
                        @Override
                        public void onAdClosed() {
                            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        }
                    });
                } else {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }
            }
        }, 4000);

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
}