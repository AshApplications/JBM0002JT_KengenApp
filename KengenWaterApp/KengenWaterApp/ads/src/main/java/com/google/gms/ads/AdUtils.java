package com.google.gms.ads;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.preference.PowerPreference;

public class AdUtils {

    public static String DEF_VALUE = "#02adff";
    public static String DEF_BUTTON_VALUE = "#000000";
    public static String DEF_BUTTON_TEXT = "#FFFFFF";
    public static String DEF_TEXT = "#000000";

    public static final String activitySplash = "activitySplash";

    public static Integer[] adsQurekaInters = new Integer[]{R.drawable.qureka_inter1, R.drawable.qureka_inter2, R.drawable.qureka_inter3, R.drawable.qureka_inter4, R.drawable.qureka_inter5};
    public static Integer[] adsQurekaGifInters = new Integer[]{R.drawable.qureka_round1, R.drawable.qureka_round2, R.drawable.qureka_round3, R.drawable.qureka_round4, R.drawable.qureka_round5};

    public static String QINTER_COUNT = "QINTER_COUNT";
    public static String QMINI_COUNT = "QMINI_COUNT";
    public static String QLARGE_COUNT = "QLARGE_COUNT";
    public static String QLIST_COUNT = "QLIST_COUNT";

    public static final String APP_OPEN_SHOW = "APP_OPEN_SHOW";

    public static String BANNERID = "GoogleBannerAds";
    public static String OPENAD = "GoogleAppOpenAds";
    public static String INTERID = "GoogleInterAds";
    public static String NATIVEID = "GoogleNativeAds";

    public static final String QUREKA_ADS = "QurekaLink";

    public static final String AdsOnOff = "AdsOnOff";
    public static final String QurekaOnOff = "QurekaOnOff";
    public static final String GoogleAdsOnOff = "GoogleAdsOnOff";

    public static final String AppOpenTime = "AppOpenTime";

    public static String APPID = "AppID";

    public static String AD_CLICK_COUNT = "AD_CLICK_COUNT";
    public static String APP_CLICK_COUNT = "APP_CLICK_COUNT";

    public static final String WhichOneSplashAppOpen = "WhichOneSplashAppOpen";
    public static final String WhichOneBannerNative = "WhichOneBannerNative";
    public static final String WhichOneAllNative = "WhichOneAllNative";
    public static final String WhichOneListNative = "WhichOneListNative";
    public static final String ListNativeAfterCount = "ListNativeAfterCount";

    public static final String SERVER_INTERVAL_COUNT = "InterIntervalCount";
    public static final String APP_INTERVAL_COUNT = "APP_INTERVAL_COUNT";

    public static final String SERVER_BACK_COUNT = "BackInterIntervalCount";
    public static final String APP_BACK_COUNT = "APP_BACK_COUNT";

    public static final String BACK_ADS = "BACK_ADS";

    public static final String ShowDialogBeforeAds = "ShowDialogBeforeAds";
    public static final String DialogTimeInSec = "DialogTimeInSec";

    public static final String LoaderNativeOnOff = "LoaderNativeOnOff";
    public static final String ExitDialogNativeOnOff = "ExitDialogNativeOnOff";

    public static void showLog(String message) {
        Log.e("errorLog", message);
    }

    public static void gotoAds(Context context) {
        try {
            String packageName = "com.android.chrome";
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorButton));
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(context, Uri.parse(PowerPreference.getDefaultFile().getString(AdUtils.QUREKA_ADS, "https://1064.win.qureka.com/")));
        } catch (Exception e) {
            AdUtils.showLog(e.toString());
        }
    }

    public static void setQureka(Activity activity, String isSmall, ImageView imageViewMain, ImageView imageViewBG, ImageView imageViewGif) {
        if (PowerPreference.getDefaultFile().getInt(isSmall, 0) >= AdUtils.adsQurekaInters.length) {
            PowerPreference.getDefaultFile().putInt(isSmall, 0);
            setQureka(activity, isSmall, imageViewMain, imageViewBG, imageViewGif);
        } else {
            if (!activity.isFinishing()) {
                if (imageViewBG != null)
                    Glide.with(activity).load(AdUtils.adsQurekaInters[PowerPreference.getDefaultFile().getInt(isSmall, 0)])
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViewBG);

                if (imageViewMain != null)
                    Glide.with(activity).load(AdUtils.adsQurekaInters[PowerPreference.getDefaultFile().getInt(isSmall, 0)])
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViewMain);

                if (imageViewGif != null)
                    Glide.with(activity).asGif().load(AdUtils.adsQurekaGifInters[PowerPreference.getDefaultFile().getInt(isSmall, 0)])
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViewGif);
            }
            int top = PowerPreference.getDefaultFile().getInt(isSmall, 0) + 1;
            PowerPreference.getDefaultFile().putInt(isSmall, top);
        }
    }

    public static void closeGoogleAds() {
        int clickCOunt = PowerPreference.getDefaultFile().getInt(AdUtils.APP_CLICK_COUNT, 0);
        PowerPreference.getDefaultFile().putInt(AdUtils.APP_CLICK_COUNT, clickCOunt + 1);
        int clickCOunt2 = PowerPreference.getDefaultFile().getInt(AdUtils.APP_CLICK_COUNT, 0);

        if (clickCOunt2 >= PowerPreference.getDefaultFile().getInt(AdUtils.AD_CLICK_COUNT, 3)) {
            PowerPreference.getDefaultFile().putBoolean(AdUtils.GoogleAdsOnOff, false);
        }
    }
}
