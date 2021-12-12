package com.water.alkaline.kengen.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;

import com.preference.PowerPreference;
import com.water.alkaline.kengen.R;

import java.io.File;

public class Constant {

    public static final String DIR = "MotivationMenia";
    public static final String notifyKey = "notifyKey";

    public static final String appShareMsg="appShareMsg";
    public static final String vidShareMsg="vidShareMsg";

    public static final String Token = "Token";
    public static Integer[] adBanners = new Integer[]{R.drawable.qureka1,
            R.drawable.qureka2, R.drawable.qureka3, R.drawable.qureka4, R.drawable.qureka5, R.drawable.qureka6, R.drawable.qureka7};
    public static Integer[] adNAtives = new Integer[]{R.drawable.nativequreka1,
            R.drawable.nativequreka2, R.drawable.nativequreka3, R.drawable.nativequreka4,
            R.drawable.nativequreka5, R.drawable.nativequreka6, R.drawable.nativequreka7};

    public static String BANNER_POSITION = "BANNER_POSITION";
    public static String INTER_POSITION = "INTER_POSITION";
    public static String NATIVE_POSITION = "NATIVE_POSITION";
    public static String NATIVELIST_POSITION = "NATIVELIST_POSITION";
    public static String APP_POSITION = "APP_POSITION";

    public static final int STORE_TYPE = 0;
    public static final int AD_TYPE = 1;
    public static final int LOADING = 2;

    public static String SPLASH_ADS = "SPLASH_ADS";
    public static String AD_DISPLAY_COUNT = "AD_DISPLAY_COUNT";
    public static String NATIVE = "NATIVE";


    public static String T_DATE = "T_DATE";

    public static String AD_CLICK_COUNT = "AD_CLICK_COUNT";
    public static String APP_CLICK_COUNT = "APP_CLICK_COUNT";

    public static String APPODEAL = "APPODEAL";
    public static String QUREKA = "QUREKA";

    public static final String SERVER_INTERVAL_COUNT = "GooglecustomIntervalCount";
    public static final String APP_INTERVAL_COUNT = "appcustomIntervalCount";

    public static String TOTAL = "TOTAL";

    public static String QUREKA_ID = "QUREKA_ID";
    public static String G_BANNERID = "G_BANNERID";
    public static String G_INTERID = "G_INTERID";
    public static String G_NATIVEID = "G_NATIVEID";
    public static String G_APPID = "G_APPID";


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

    public static final String POSITION = "POSITION";
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
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(context, Uri.parse(PowerPreference.getDefaultFile().getString(Constant.QUREKA_ID, "")));
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
        File myCreationDir = new File(dCimDirPath, DIR + "/SavedPDF");
        if (!myCreationDir.exists())
            myCreationDir.mkdirs();

        return String.valueOf(myCreationDir);
    }

    public static String getImagedisc() {
        File dCimDirPath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

        if (!dCimDirPath.exists())
            dCimDirPath.mkdir();
        File myCreationDir = new File(dCimDirPath, DIR + "/SavedIamges");
        if (!myCreationDir.exists())
            myCreationDir.mkdirs();

        return String.valueOf(myCreationDir);
    }


}
