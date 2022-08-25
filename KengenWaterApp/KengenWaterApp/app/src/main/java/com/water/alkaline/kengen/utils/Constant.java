package com.water.alkaline.kengen.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

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

    public static String T_DATE = "T_DATE";

    public static String AD_CLICK_COUNT = "AD_CLICK_COUNT";
    public static String APP_CLICK_COUNT = "APP_CLICK_COUNT";

    public static String OPENAD = "GoogleAppOpenAds";
    public static String INTERID = "GoogleInterAds";
    public static String NATIVEID = "GoogleNativeAds";
    public static String BANNERID = "GoogleBannerAds";

    public static final String QUREKA_ADS = "QurekaLink";

    public static final String AdsOnOff = "AdsOnOff";
    public static final String GoogleAdsOnOff = "GoogleAdsOnOff";
    public static final String QurekaOnOff = "QurekaOnOff";

    public static final String AppOpen = "AppOpen";

    public static final String GoogleSplashOpenAdsOnOff = "GoogleSplashOpenAdsOnOff";
    public static final String GoogleExitSplashInterOnOff = "GoogleExitSplashInterOnOff";
    public static final String GoogleAppOpenAdsOnOff = "GoogleAppOpenAdsOnOff";

    public static final String SERVER_INTERVAL_COUNT = "SERVER_INTERVAL_COUNT";
    public static final String APP_INTERVAL_COUNT = "APP_INTERVAL_COUNT";

    public static final String SERVER_BACK_COUNT = "SERVER_BACK_COUNT";
    public static final String APP_BACK_COUNT = "APP_BACK_COUNT";

    public static final String BACK_ADS = "BACK_ADS";

    public static final String GoogleBannerOnOff = "GoogleBannerOnOff";
    public static final String GoogleInterOnOff = "GoogleInterOnOff";
    public static final String GoogleBackInterOnOff = "GoogleBackInterOnOff";

    public static final String GoogleMiniNativeOnOff = "GoogleMiniNativeOnOff";
    public static final String GoogleLargeNativeOnOff = "GoogleLargeNativeOnOff";
    public static final String GoogleListNativeOnOff = "GoogleListNativeOnOff";

    public static final String BannerAdWhichOne = "BannerAdWhichOne";
    public static final String ListNativeWhichOne = "ListNativeWhichOne";
    public static final String ListNativeAfterCount = "ListNativeAfterCount";
    ;

    public static final String QurekaIconOnOff = "QurekaIconOnOff";
    public static final String QurekaBannerOnOff = "QurekaBannerOnOff";
    public static final String QurekaInterOnOff = "QurekaInterOnOff";
    public static final String QurekaBackInterOnOff = "QurekaBackInterOnOff";
    public static final String QurekaMiniNativeOnOff = "QurekaMiniNativeOnOff";
    public static final String QurekaLargeNativeOnOff = "QurekaLargeNativeOnOff";
    public static final String QurekaListNativeOnOff = "QurekaListNativeOnOff";
    public static final String QurekaAppOpenOnOff = "QurekaAppOpenOnOff";

    public static final String ShowDialogBeforeAds = "ShowDialogBeforeAds";
    public static final String DialogTimeInSec = "DialogTimeInSec";

    public static final String VpnOnOff = "VpnOnOff";
    public static final String VpnAuto = "VpnAuto";
    public static final String VpnUrl = "VpnUrl";

    public static final String adsCloseCount = "adsCloseCount";

    public static final String QBANNER_COUNT = "QBANNER_COUNT";
    public static final String QLARGE_COUNT = "QLARGE_COUNT";
    public static final String QMINI_COUNT = "QMINI_COUNT";
    public static final String QLIST_COUNT = "QLIST_COUNT";
    public static final String QINTER_COUNT = "QINTER_COUNT";
    public static final String QBACKINTER_COUNT = "QBACKINTER_COUNT";
    public static final String QICON_COUNT = "QICON_COUNT";
    public static final String QOPEN_COUNT = "QOPEN_COUNT";

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
            Constant.showLog(e.toString());
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
        File myCreationDir = new File(dCimDirPath, MyApplication.getContext().getString(R.string.app_name) + "/SavedPDF");
        if (!myCreationDir.exists())
            myCreationDir.mkdirs();

        return String.valueOf(myCreationDir);
    }

    public static String getImagedisc() {
        File dCimDirPath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

        if (!dCimDirPath.exists())
            dCimDirPath.mkdir();
        File myCreationDir = new File(dCimDirPath, MyApplication.getContext().getString(R.string.app_name) + "/SavedIamges");
        if (!myCreationDir.exists())
            myCreationDir.mkdirs();

        return String.valueOf(myCreationDir);
    }

    public static boolean isVpnConnected() {
        String iface = "";
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    iface = networkInterface.getName();

                if (iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
                    return true;
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }

        return false;
    }


    public static void setQurekaIcon(Activity activity, ImageView imageView, String data) {
        if (PowerPreference.getDefaultFile().getInt(data, 0) >= 13) {
            PowerPreference.getDefaultFile().putInt(data, 0);
            setQurekaIcon(activity, imageView, data);
        } else {
            int top = PowerPreference.getDefaultFile().getInt(data, 0) + 1;
            PowerPreference.getDefaultFile().putInt(data, top);

            if (!activity.isFinishing()) {
                Glide.with(activity).asGif().load(MyApplication.getIcon(activity) + PowerPreference.getDefaultFile().getInt(data, 1) + ".gif")
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
            }
        }
    }

    public static void setQureka(Activity activity, ImageView imageViewMain, ImageView imageViewBG, ImageView imageViewGif, String isSmall) {

        if (PowerPreference.getDefaultFile().getInt(isSmall, 0) >= 5) {
            PowerPreference.getDefaultFile().putInt(isSmall, 0);
            setQureka(activity, imageViewMain, imageViewBG, imageViewGif, isSmall);
        } else {

            int top = PowerPreference.getDefaultFile().getInt(isSmall, 0) + 1;
            PowerPreference.getDefaultFile().putInt(isSmall, top);

            if (imageViewBG != null && !activity.isFinishing())
                Glide.with(activity).load(MyApplication.getInter(activity) + PowerPreference.getDefaultFile().getInt(isSmall, 1) + ".webp")
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViewBG);

            if (imageViewMain != null && !activity.isFinishing())
                Glide.with(activity).load(MyApplication.getInter(activity) + PowerPreference.getDefaultFile().getInt(isSmall, 1) + ".webp")
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViewMain);

            if (imageViewGif != null && !activity.isFinishing())
                Glide.with(activity).asGif().load(MyApplication.getRound(activity) + PowerPreference.getDefaultFile().getInt(isSmall, 1) + ".gif")
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViewGif);


        }
    }

    public static void setQurekaBanner(Activity activity, ImageView imageViewMain, String isSmall) {

        if (PowerPreference.getDefaultFile().getInt(isSmall, 0) >= 7) {
            PowerPreference.getDefaultFile().putInt(isSmall, 0);
            setQurekaBanner(activity, imageViewMain, isSmall);
        } else {

            int top = PowerPreference.getDefaultFile().getInt(isSmall, 0) + 1;
            PowerPreference.getDefaultFile().putInt(isSmall, top);

            if (imageViewMain != null && !activity.isFinishing())
                Glide.with(activity).load(MyApplication.getBanner(activity) + PowerPreference.getDefaultFile().getInt(isSmall, 1) + ".webp")
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViewMain);

        }
    }

    public static void checkIcon(Activity activity) {
        if (activity.findViewById(R.id.ivToolGif) != null) {
            if (PowerPreference.getDefaultFile().getBoolean(Constant.QurekaOnOff, false) && PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, false) && PowerPreference.getDefaultFile().getBoolean(Constant.QurekaIconOnOff, false)) {
                Constant.setQurekaIcon(activity, (ImageView) activity.findViewById(R.id.ivToolGif), Constant.QICON_COUNT);
                activity.findViewById(R.id.ivToolGif).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.ivToolGif).setOnClickListener(v -> Constant.gotoAds(activity));
            }
        }
    }

}
