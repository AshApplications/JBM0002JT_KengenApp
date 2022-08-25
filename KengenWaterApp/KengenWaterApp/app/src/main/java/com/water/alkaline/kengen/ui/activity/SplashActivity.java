package com.water.alkaline.kengen.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import com.water.alkaline.kengen.placements.LargeNativeAds;
import com.water.alkaline.kengen.placements.ListNativeAds;
import com.water.alkaline.kengen.placements.MiniNativeAds;
import com.water.alkaline.kengen.placements.NewOpenAds;
import com.water.alkaline.kengen.placements.OpenAds;
import com.water.alkaline.kengen.utils.Constant;
import com.preference.PowerPreference;


import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    public static SplashActivity activity;
    ActivitySplashBinding binding;

    int REQUEST_PERMISSIONS = 200;
    boolean check = false;
    public AppViewModel viewModel;

    int VERSION = 0;

    public Dialog dialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    public void setBG() {
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
    }

    public Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (!SplashActivity.this.isFinishing()) {
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
                                mainAPI();
                            } else dialog.show();
                        }
                    });
                }
            } else {
                Log.e("TAG", "Something went wrong");
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
        activity = this;
        PowerPreference.getDefaultFile().putInt(Constant.mIsDuration, 1);

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
                        checkVpn();
                    }
                }).start();
    }

    public void checkVpn() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (Constant.isVpnConnected()) {
                    network_dialog("VPN is Connected Please Turn it Off & Try Again !")
                            .txtRetry.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dismiss_dialog();
                                    checkVpn();
                                }
                            });
                } else if (MyApplication.getMain(SplashActivity.this).equalsIgnoreCase("")) {
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

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(@NonNull String s) {
                PowerPreference.getDefaultFile().putString(Constant.Token, s);
                updateAPI();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getToken();
                    }
                }, 2000);
            }
        });
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

                                    PowerPreference.getDefaultFile().putString(Constant.BANNERID, appData.getGoogleBannerAds());
                                    PowerPreference.getDefaultFile().putString(Constant.INTERID, appData.getGoogleInterAds());
                                    PowerPreference.getDefaultFile().putString(Constant.NATIVEID, appData.getGoogleNativeAds());
                                    PowerPreference.getDefaultFile().putString(Constant.OPENAD, appData.getGoogleAppOpenAds());


                                    try {
                                        ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                                        Bundle bundle = ai.metaData;
                                        String myApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                                        ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", appData.getGoogleAppIdAds());//you can replace your key APPLICATION_ID here
                                        String ApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                                    } catch (PackageManager.NameNotFoundException e) {
                                        Log.e("TAG", "Failed to load meta-data, NameNotFound: " + e.getMessage());
                                    } catch (NullPointerException e) {
                                        Log.e("TAG", "Failed to load meta-data, NullPointer: " + e.getMessage());
                                    }

                                    MobileAds.initialize(SplashActivity.this);


                                    PowerPreference.getDefaultFile().putBoolean(Constant.AdsOnOff, appData.getAdsOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.GoogleAdsOnOff, appData.getGoogleAdsOnOff());
                                    PowerPreference.getDefaultFile().putBoolean(Constant.QurekaOnOff, appData.getQurekaOnOff());

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

                                    if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, false)) {
                                        new BackInterAds().loadInterAds(SplashActivity.this);
                                        new BannerAds().loadBannerAds(SplashActivity.this);
                                        new InterAds().loadInterAds(SplashActivity.this);
                                        new LargeNativeAds().loadNativeAds(SplashActivity.this);
                                        new ListNativeAds().loadNativeAds(SplashActivity.this);
                                        new MiniNativeAds().loadNativeAds(SplashActivity.this);
                                        new OpenAds().loadOpenAd();
                                        new NewOpenAds().loadOpenAd(SplashActivity.this);
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

                        getPermissions();

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
                .create()
                .show();
    }

    private void forcePermissionDialog(String s, DialogInterface.OnClickListener aPackage) {
        new AlertDialog.Builder(SplashActivity.this)
                .setTitle("Permission Denied")
                .setMessage(s)
                .setPositiveButton("Give Permission", aPackage)
                .create()
                .show();
    }


    public void nextActivity() {

        FirebaseMessaging.getInstance().subscribeToTopic(PowerPreference.getDefaultFile().getString(Constant.notifyKey, "kengen")).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleSplashOpenAdsOnOff, false)) {
                    new NewOpenAds().showOpenAd(SplashActivity.this, new NewOpenAds.OnAdClosedListener() {
                        @Override
                        public void onAdClosed() {
                            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        }
                    });
                } else
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            }
        }, 4000);

    }



}