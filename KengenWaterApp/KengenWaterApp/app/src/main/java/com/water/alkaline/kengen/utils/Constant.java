package com.water.alkaline.kengen.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ForwardScope;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.google.gms.ads.AdUtils;
import com.google.gms.ads.MainAds;
import com.water.alkaline.kengen.databinding.DialogExitBinding;
import com.water.alkaline.kengen.ui.activity.ExitActivity;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class Constant {

    public static final String notifyKey = "notifyKey";
    public static final String apiKey = "apiKey";

    public static final String isRunning = "isRunning";

    public static final String appShareMsg = "appShareMsg";
    public static final String vidShareMsg = "vidShareMsg";

    public static final String Token = "Token";
    public static final String mToken = "mToken";

    public static final int STORE_TYPE = 0;
    public static final int AD_TYPE = 1;
    public static final int LOADING = 2;

    public static final String mKeyId = "mKeyId";
    public static final String mChannelID = "mChannelID";
    public static final String mIsChannel = "mIsChannel";
    public static final String LIVE = "LIVE";
    public static final String mFeeds = "mFeeds";
    public static final String mList = "mList";
    public static final String mBanners = "mList";
    public static final String mNotice = "mNotice";
    public static final String mIsDuration = "mIsDuration";
    public static final String mIsApi = "mIsApi";
    public static final String POSITION = "POSITION";
    public static final String mIsLoaded = "mIsLoaded";
    public static String T_DATE = "T_DATE";

    public static int TYPE_PDF = 0;
    public static int TYPE_IMAGE = 1;

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showLog(String text) {
        Log.e("logError", text);
    }

    public static void Log(String text) {
        Log.e("logError", text);
    }

    public static boolean checkInternet(Context activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static String getPDFdisc() {
        File myCreationDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + MyApplication.getContext().getString(R.string.app_name) + File.separator + "SavedPDF");

        if (!myCreationDir.exists())
            myCreationDir.mkdirs();

        return myCreationDir.getAbsolutePath();
    }

    public static String getImagedisc() {
        File myCreationDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + MyApplication.getContext().getString(R.string.app_name) + File.separator + "SavedImages");

        if (!myCreationDir.exists())
            myCreationDir.mkdirs();

        return myCreationDir.getAbsolutePath();
    }

    public static void rateUs(Activity activity) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
        } catch (ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }

    public static void showRateDialog(Activity activity, boolean isAds) {
        try {
            Dialog mDialog = new Dialog(activity, R.style.NormalDialog);
            DialogExitBinding exitBinding = DialogExitBinding.inflate(activity.getLayoutInflater());
            mDialog.setContentView(exitBinding.getRoot());
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            exitBinding.btnRate.setOnClickListener(view -> {
                mDialog.dismiss();
                Constant.rateUs(activity);
            });

            exitBinding.btnExiy.setOnClickListener(view -> {
                mDialog.dismiss();
                Intent intent = new Intent(activity, ExitActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("isAds", isAds);
                activity.startActivity(intent);
                activity.finish();

            });

            exitBinding.btnCancel.setOnClickListener(view -> {
                mDialog.dismiss();
            });

            if (isAds) {
                mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        if (PowerPreference.getDefaultFile().getBoolean(AdUtils.ExitDialogNativeOnOff, false)) {
                            new MainAds().showNativeAds(activity, mDialog, exitBinding.includedAd.adFrameLarge, exitBinding.includedAd.adSpaceLarge);
                        } else {
                            exitBinding.includedAd.adFrameLarge.removeAllViews();
                        }
                    }
                });
            }
            mDialog.show();

        } catch (Exception e) {
            Log.w("Catch", Objects.requireNonNull(e.getMessage()));
        }
    }

    public static void getPermissions(AppCompatActivity activity) {
        PermissionX.init(activity)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .explainReasonBeforeRequest()
                .onForwardToSettings(new ForwardToSettingsCallback() {
                    @Override
                    public void onForwardToSettings(@NonNull ForwardScope scope, @NonNull List<String> deniedList) {
                        scope.showForwardToSettingsDialog(deniedList, "You need to allow necessary permissions in Settings manually", "OK", "Cancel");
                    }
                }).request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (allGranted) {
                            Toast.makeText(activity, "All Permissions are Granted", Toast.LENGTH_LONG).show();
                        } else if (deniedList.size() > 0) {
                            Toast.makeText(activity, "These Permissions are Denied: " + deniedList.get(0), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(activity, "Permissions are Denied", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public static boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

}
