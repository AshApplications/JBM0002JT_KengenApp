package com.water.alkaline.kengen.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.water.alkaline.kengen.R;

import java.io.File;

public class Constant {
    public static final String mKeyId = "mKeyId";
    public static final String mChannelID = "mChannelID";
    public static final String mIsChannel = "mIsChannel";

    public static final String SAVE = "SAVE";
    public static final String LIVE = "LIVE";


    public static final String mFeeds = "mFeeds";
    public static final String mList = "mList";
    public static final String mBanners = "mList";

    public static final String mNotice = "mNotice";
    public static final String mIsDuration = "mIsDuration";
    public static final String mIsApi = "mIsApi";
    public static final String mPosition = "mPosition";
    public static final String isSaved = "isSaved";


    public static String isRunning = "isRunning";

    public static int TYPE_PDF = 0;
    public static int TYPE_IMAGE = 1;

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showLog(String text) {
        Log.e("logError", text);
    }

    public static void gotoAds(Context context) {
        try {
            String packageName = "com.android.chrome";
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryLight));
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(context, Uri.parse("https://127.game.qureka.com"));
        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }
    }

    public static boolean checkInternet(Context activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static String getPDFdisc() {
        File dCimDirPath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

        if (!dCimDirPath.exists())
            dCimDirPath.mkdir();
        File myCreationDir = new File(dCimDirPath, "KenGen/SavedPDF");
        if (!myCreationDir.exists())
            myCreationDir.mkdirs();

        return String.valueOf(myCreationDir);
    }

    public static String getImagedisc() {
        File dCimDirPath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

        if (!dCimDirPath.exists())
            dCimDirPath.mkdir();
        File myCreationDir = new File(dCimDirPath, "KenGen/SavedIamges");
        if (!myCreationDir.exists())
            myCreationDir.mkdirs();

        return String.valueOf(myCreationDir);
    }


}
