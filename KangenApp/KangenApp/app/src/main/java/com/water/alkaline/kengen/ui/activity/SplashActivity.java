package com.water.alkaline.kengen.ui.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.airbnb.lottie.value.SimpleLottieValueCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.water.alkaline.kengen.BuildConfig;
import com.water.alkaline.kengen.MyService;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.data.network.RetroClient;
import com.water.alkaline.kengen.databinding.ActivitySplashBinding;
import com.water.alkaline.kengen.databinding.DialogInternetBinding;
import com.water.alkaline.kengen.library.ViewAnimator.AnimationListener;
import com.water.alkaline.kengen.library.ViewAnimator.ViewAnimator;
import com.water.alkaline.kengen.model.ObjectSerializer;
import com.water.alkaline.kengen.model.feedback.Feedback;
import com.water.alkaline.kengen.model.feedback.FeedbackResponse;
import com.water.alkaline.kengen.model.main.MainResponse;
import com.water.alkaline.kengen.model.update.AppInfo;
import com.water.alkaline.kengen.model.update.UpdateResponse;
import com.water.alkaline.kengen.utils.Constant;
import com.preference.PowerPreference;

import java.util.ArrayList;
import java.util.List;
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
        startService(new Intent(this, MyService.class));

        ViewAnimator.animate(binding.ivLogo)
                .scale(0f, 1f)
                .andAnimate(binding.llText)
                .alpha(0f,1f)
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


    public void updateError() {
        network_dialog(getResources().getString(R.string.error_internet)).txtRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss_dialog();
                if (Constant.checkInternet(SplashActivity.this)) {
                    updateAPI();
                } else dialog.show();
            }
        });
    }

    public void updateAPI() {
        if (Constant.checkInternet(SplashActivity.this)) {
            RetroClient.getInstance().getApi().updateApi("update")
                    .enqueue(new Callback<UpdateResponse>() {
                        @Override
                        public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                            try {
                                final UpdateResponse updateResponse = response.body();

                                AppInfo appInfo = updateResponse.getData().getAppInfo().get(0);
                                PowerPreference.getDefaultFile().putString(Constant.mKeyId, appInfo.getApiKey());
                                PowerPreference.getDefaultFile().putString(Constant.mNotice, appInfo.getAppNotice());

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
                                    userAPI();
                                }

                            } catch (Exception e) {
                                Constant.showLog(e.toString());
                                e.printStackTrace();
                                Constant.showToast(SplashActivity.this, "Something went Wrong");
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateResponse> call, Throwable t) {
                            updateError();
                        }
                    });
        } else {
            updateError();
        }
    }

    public void userError() {
        network_dialog(getResources().getString(R.string.error_internet)).txtRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss_dialog();
                if (Constant.checkInternet(SplashActivity.this)) {
                    userAPI();
                } else dialog.show();
            }
        });
    }

    public void userAPI() {
        if (Constant.checkInternet(SplashActivity.this)) {
            @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            RetroClient.getInstance().getApi().userApi(deviceId, BuildConfig.VERSION_CODE)
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
                            userError();
                        }
                    });
        } else {
            userError();
        }
    }

    public void mainError() {
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
                    mainError();
                }
            });
        } else {
            mainError();
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

        FirebaseMessaging.getInstance().subscribeToTopic("kangen").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));

            }
        }, 4000);

    }


}