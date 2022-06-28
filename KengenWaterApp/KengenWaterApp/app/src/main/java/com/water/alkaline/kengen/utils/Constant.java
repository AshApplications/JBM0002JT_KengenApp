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

    public static final String appShareMsg = "appShareMsg";
    public static final String vidShareMsg = "vidShareMsg";

    public static final String Token = "Token";

    public static Integer[] adsQurekaInters = new Integer[]{R.drawable.qureka_inter1, R.drawable.qureka_inter2, R.drawable.qureka_inter3, R.drawable.qureka_inter4, R.drawable.qureka_inter5};
    public static Integer[] adsQurekaGifInters = new Integer[]{R.drawable.qureka_round1, R.drawable.qureka_round2, R.drawable.qureka_round3, R.drawable.qureka_round4, R.drawable.qureka_round5};


    public static final int STORE_TYPE = 0;
    public static final int AD_TYPE = 1;
    public static final int LOADING = 2;

    public static String T_DATE = "T_DATE";

    public static String AD_CLICK_COUNT = "AD_CLICK_COUNT";
    public static String APP_CLICK_COUNT = "APP_CLICK_COUNT";

    public static String OPENAD = "GoogleAppOpenAds";
    public static String INTERID = "GoogleInterAds";
    public static String NATIVEID = "GoogleNativeAds";


    public static final String QUREKA_ADS = "QurekaLink";

    public static final String AdsOnOff = "AdsOnOff";
    public static final String GoogleAdsOnOff = "GoogleAdsOnOff";
    public static final String QurekaOnOff = "QurekaOnOff";

    public static final String GoogleSplashOpenAdsOnOff = "GoogleSplashOpenAdsOnOff";
    public static final String GoogleExitSplashInterOnOff = "GoogleExitSplashInterOnOff";
    public static final String GoogleAppOpenAdsOnOff = "GoogleAppOpenAdsOnOff";

    public static final String SERVER_INTERVAL_COUNT = "SERVER_INTERVAL_COUNT";
    public static final String APP_INTERVAL_COUNT = "APP_INTERVAL_COUNT";

    public static final String SERVER_BACK_COUNT = "SERVER_BACK_COUNT";
    public static final String APP_BACK_COUNT = "APP_BACK_COUNT";

    public static final String BACK_ADS = "BACK_ADS";

    public static final String GoogleInterOnOff = "GoogleInterOnOff";
    public static final String GoogleBackInterOnOff = "GoogleBackInterOnOff";

    public static final String GoogleMiniNativeOnOff = "GoogleMiniNativeOnOff";
    public static final String GoogleLargeNativeOnOff = "GoogleLargeNativeOnOff";
    public static final String GoogleListNativeOnOff = "GoogleListNativeOnOff";

    public static final String ListNativeWhichOne = "ListNativeWhichOne";
    public static final String ListNativeAfterCount = "ListNativeAfterCount";
    ;

    public static final String QurekaInterOnOff = "QurekaInterOnOff";
    public static final String QurekaBackInterOnOff = "QurekaBackInterOnOff";
    public static final String QurekaMiniNativeOnOff = "QurekaMiniNativeOnOff";
    public static final String QurekaLargeNativeOnOff = "QurekaLargeNativeOnOff";
    public static final String QurekaListNativeOnOff = "QurekaListNativeOnOff";
    public static final String QurekaAppOpenOnOff = "QurekaAppOpenOnOff";

    public static final String ShowDialogBeforeAds = "ShowDialogBeforeAds";
    public static final String DialogTimeInSec = "DialogTimeInSec";

    public static final String VpnOnOff = "VpnOnOff";
    public static final String VpnUrl = "VpnUrl";

    public static final String adsCloseCount = "adsCloseCount";

    public static final String QBANNER_COUNT = "QBANNER_COUNT";
    public static final String QLARGE_COUNT = "QLARGE_COUNT";
    public static final String QMINI_COUNT = "QMINI_COUNT";
    public static final String QLIST_COUNT = "QLIST_COUNT";
    public static final String QINTER_COUNT = "QINTER_COUNT";
    public static final String QBACKINTER_COUNT = "QBACKINTER_COUNT";

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

    public static void Log(String text) {
        Log.e("logError", text);
    }

    public static void gotoAds(Context context) {
        try {
            String packageName = "com.android.chrome";
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(context, Uri.parse(PowerPreference.getDefaultFile().getString(Constant.QUREKA_ADS, "")));
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
