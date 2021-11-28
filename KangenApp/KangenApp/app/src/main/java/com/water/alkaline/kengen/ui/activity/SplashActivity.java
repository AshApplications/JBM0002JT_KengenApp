package com.water.alkaline.kengen.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.Native;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.water.alkaline.kengen.BuildConfig;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.MyService;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.data.network.RetroClient;
import com.water.alkaline.kengen.databinding.ActivitySplashBinding;
import com.water.alkaline.kengen.databinding.DialogInternetBinding;
import com.water.alkaline.kengen.library.ViewAnimator.AnimationListener;
import com.water.alkaline.kengen.library.ViewAnimator.ViewAnimator;
import com.water.alkaline.kengen.model.feedback.FeedbackResponse;
import com.water.alkaline.kengen.model.main.MainResponse;
import com.water.alkaline.kengen.model.update.AdsInfo;
import com.water.alkaline.kengen.model.update.AppInfo;
import com.water.alkaline.kengen.model.update.UpdateResponse;
import com.water.alkaline.kengen.placements.BannerAds;
import com.water.alkaline.kengen.placements.InterAds;
import com.water.alkaline.kengen.placements.NativeAds;
import com.water.alkaline.kengen.placements.OpenAds;
import com.water.alkaline.kengen.utils.Constant;
import com.preference.PowerPreference;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;

    int REQUEST_PERMISSIONS = 200;
    boolean check = false;
    public AppViewModel viewModel;

    public Dialog dialog;

    public void setBG() {
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        Glide.with(this).load(R.drawable.bg).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivBG);
    }


    public Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == 1000) {
                network_dialog(getResources().getString(R.string.error_internet)).txtRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss_dialog();
                        if (Constant.checkInternet(SplashActivity.this)) {
                            updateAPI();
                        } else dialog.show();
                    }
                });
            } else if (msg.what == 1001) {
                network_dialog(getResources().getString(R.string.error_internet)).txtRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss_dialog();
                        if (Constant.checkInternet(SplashActivity.this)) {
                            userAPI();
                        } else dialog.show();
                    }
                });
            } else if (msg.what == 1002) {
                network_dialog(getResources().getString(R.string.error_internet)).txtRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss_dialog();
                        if (Constant.checkInternet(SplashActivity.this)) {
                            mainAPI();
                        } else dialog.show();
                    }
                });
            }
        }
    };

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
                        updateAPI();
                    }
                }).start();


    }

    public void updateAPI() {
        if (Constant.checkInternet(SplashActivity.this)) {
            RetroClient.getInstance().getApi().updateApi("update")
                    .enqueue(new Callback<UpdateResponse>() {
                        @Override
                        public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                            try {
                                final UpdateResponse updateResponse = response.body();

                                AdsInfo adsInfo = updateResponse.getData().getAdsInfo().get(0);

                                MobileAds.initialize(
                                        SplashActivity.this,
                                        initializationStatus -> {

                                        });
                                RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("FB01FB6E16B9C9704083088076C527FC")).build();
                                MobileAds.setRequestConfiguration(configuration);
                                PowerPreference.getDefaultFile().putInt(Constant.SERVER_INTERVAL_COUNT, Integer.parseInt(adsInfo.getIntervalCount()));
                                PowerPreference.getDefaultFile().putInt(Constant.APP_INTERVAL_COUNT, 1);

                                PowerPreference.getDefaultFile().putInt(Constant.AD_DISPLAY_COUNT, Integer.valueOf(adsInfo.getNativeCount()));
                                PowerPreference.getDefaultFile().putInt(Constant.NATIVE, Integer.valueOf(adsInfo.getNativeOn()));

                                PowerPreference.getDefaultFile().putInt(Constant.AD_CLICK_COUNT, Integer.valueOf(adsInfo.getClickCount()));
                                PowerPreference.getDefaultFile().putInt(Constant.QUREKA, Integer.valueOf(adsInfo.getQurekaOn()));

                                String adsPrio = adsInfo.getAdPriority();
                                String[] separated = adsPrio.split(",");
                                for (String s : separated) {
                                    MyApplication.arrayList.add(Integer.parseInt(s));
                                }

                                PowerPreference.getDefaultFile().putInt(Constant.TOTAL, separated.length);
                                PowerPreference.getDefaultFile().putInt(Constant.BANNER_POSITION, -1);
                                PowerPreference.getDefaultFile().putInt(Constant.INTER_POSITION, -1);
                                PowerPreference.getDefaultFile().putInt(Constant.NATIVE_POSITION, -1);
                                PowerPreference.getDefaultFile().putInt(Constant.NATIVELIST_POSITION, -1);
                                PowerPreference.getDefaultFile().putInt(Constant.APP_POSITION, -1);


                                PowerPreference.getDefaultFile().putInt(Constant.SPLASH_ADS, Integer.valueOf(adsInfo.getSplashAds()));
                                PowerPreference.getDefaultFile().putString(Constant.APPODEAL, adsInfo.getOdealAppId());

                                Appodeal.setAutoCache(Appodeal.INTERSTITIAL, false);
                                Appodeal.setAutoCache(Appodeal.BANNER_VIEW, false);
                                Appodeal.setAutoCache(Appodeal.NATIVE, false);

                                Appodeal.setRequiredNativeMediaAssetType(Native.MediaAssetType.ALL);

                                PowerPreference.getDefaultFile().putString(Constant.G_BANNERID, adsInfo.getGbanner());
                                PowerPreference.getDefaultFile().putString(Constant.G_INTERID, adsInfo.getGinter());
                                PowerPreference.getDefaultFile().putString(Constant.G_NATIVEID, adsInfo.getGnative());
                                PowerPreference.getDefaultFile().putString(Constant.G_APPID, adsInfo.getGopen());

                                Appodeal.initialize(SplashActivity.this, PowerPreference.getDefaultFile().getString(Constant.APPODEAL), Appodeal.BANNER_VIEW);
                                Appodeal.initialize(SplashActivity.this, PowerPreference.getDefaultFile().getString(Constant.APPODEAL), Appodeal.INTERSTITIAL);
                                Appodeal.initialize(SplashActivity.this, PowerPreference.getDefaultFile().getString(Constant.APPODEAL), Appodeal.NATIVE);

                                new BannerAds().loadBanner(SplashActivity.this);
                                new InterAds().loadInter(SplashActivity.this);
                                new NativeAds().loadnative(SplashActivity.this);
                                new OpenAds(MyApplication.getInstance());

                                AppInfo appInfo = updateResponse.getData().getAppInfo().get(0);
                                PowerPreference.getDefaultFile().putString(Constant.mKeyId, appInfo.getApiKey());
                                PowerPreference.getDefaultFile().putString(Constant.mNotice, appInfo.getAppNotice());

                                PowerPreference.getDefaultFile().putString(Constant.notifyKey, appInfo.getNotifyKey());
                                PowerPreference.getDefaultFile().putString(Constant.QUREKA_ID, appInfo.getQureka());

                                if (!PowerPreference.getDefaultFile().getString(Constant.T_DATE, "not").equalsIgnoreCase(appInfo.getTodayDate())) {
                                    PowerPreference.getDefaultFile().putString(Constant.T_DATE, appInfo.getTodayDate());
                                    PowerPreference.getDefaultFile().putInt(Constant.APP_CLICK_COUNT, 0);
                                } else {
                                    int clickCOunt2 = PowerPreference.getDefaultFile().getInt(Constant.APP_CLICK_COUNT, 0);

                                    if (clickCOunt2 >= PowerPreference.getDefaultFile().getInt(Constant.AD_CLICK_COUNT, 3)) {
                                        PowerPreference.getDefaultFile().putInt(Constant.QUREKA, 99999);
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
                                    if (BuildConfig.VERSION_CODE != Integer.parseInt(appInfo.getVersion())) {
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
                                    if (BuildConfig.VERSION_CODE != Integer.parseInt(appInfo.getVersion())) {
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
                                    getToken();
                                }

                            } catch (Exception e) {
                                Constant.showLog(e.toString());
                                e.printStackTrace();
                                Constant.showToast(SplashActivity.this, "Something went Wrong");
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateResponse> call, Throwable t) {
                            handler.sendEmptyMessage(1000);
                        }
                    });
        } else {
            handler.sendEmptyMessage(1000);
        }
    }


    public void getToken() {

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(@NonNull @NotNull String s) {
                PowerPreference.getDefaultFile().putString(Constant.Token, s);
                userAPI();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getToken();
                    }
                },1000);
            }
        });
    }

    public void userAPI() {
        if (Constant.checkInternet(SplashActivity.this)) {

            @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            RetroClient.getInstance().getApi().userApi(deviceId, PowerPreference.getDefaultFile().getString(Constant.Token, ""), BuildConfig.VERSION_CODE)
                    .enqueue(new Callback<FeedbackResponse>() {
                        @Override
                        public void onResponse(Call<FeedbackResponse> call, Response<FeedbackResponse> response) {

                            try {
                                FeedbackResponse response1 = response.body();
                                if (response1 != null && response1.feedbacks != null) {
                                    PowerPreference.getDefaultFile().putString(Constant.mFeeds, new Gson().toJson(response1.feedbacks));
                                }
                            } catch (Exception e) {
                                Constant.showLog(e.toString());
                                e.printStackTrace();
                                Constant.showToast(SplashActivity.this, "Something went Wrong");
                            }
                            mainAPI();
                        }

                        @Override
                        public void onFailure(Call<FeedbackResponse> call, Throwable t) {
                            handler.sendEmptyMessage(1001);
                        }
                    });
        } else {
            handler.sendEmptyMessage(1001);
        }
    }

    public void mainAPI() {
        if (Constant.checkInternet(SplashActivity.this)) {
            RetroClient.getInstance().getApi().dataApi().enqueue(new Callback<MainResponse>() {
                @Override
                public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                    try {
                        final MainResponse mainResponse = response.body();

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

                        getPermissions();

                    } catch (Exception e) {
                        Constant.showLog(e.toString());
                        e.printStackTrace();
                        Constant.showToast(SplashActivity.this, "Something went Wrong");
                    }
                }

                @Override
                public void onFailure(Call<MainResponse> call, Throwable t) {
                    handler.sendEmptyMessage(1002);
                }
            });
        } else {
            handler.sendEmptyMessage(1002);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (check) {
            getPermissions();
        }
    }

    public void getPermissions() {
        check = false;
        if (checkPermissions()) {
            nextActivity();
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0) {

                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeD = shouldShowRequestPermissionRationale(permissions[0]);

                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean readD = shouldShowRequestPermissionRationale(permissions[1]);

                if (write && read) {
                    nextActivity();
                } else if (!writeD || !readD) {
                    forcePermissionDialog("You need to allow access to the permissions. Without this permission you can't access your storage. Are you sure deny this permission?",
                            (dialog, which) -> {
                                check = true;
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            });
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            oKCancelDialog("You need to allow access to the permissions",
                                    (dialog, which) -> {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                                REQUEST_PERMISSIONS);
                                    });
                        }
                    }
                }
            }
        }
    }

    private void oKCancelDialog(String s, DialogInterface.OnClickListener o) {
        new AlertDialog.Builder(SplashActivity.this)
                .setMessage(s)
                .setPositiveButton("OK", o)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void forcePermissionDialog(String s, DialogInterface.OnClickListener aPackage) {
        new AlertDialog.Builder(SplashActivity.this)
                .setTitle("Permission Denied")
                .setMessage(s)
                .setPositiveButton("Give Permission", aPackage)
                .setNegativeButton("Deny Permission", null)
                .create()
                .show();
    }


    public void nextActivity() {

        FirebaseMessaging.getInstance().subscribeToTopic(PowerPreference.getDefaultFile().getString(Constant.notifyKey, "kengen")).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });

        if (PowerPreference.getDefaultFile().getInt("isBackground", 5) > 0) {
            startService(new Intent(this, MyService.class));
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PowerPreference.getDefaultFile().getInt(Constant.SPLASH_ADS, 4) <= 0) {
                    PowerPreference.getDefaultFile().putInt(Constant.APP_INTERVAL_COUNT, 0);
                    new InterAds().showInter(SplashActivity.this, new InterAds.OnAdClosedListener() {
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


}