package com.water.alkaline.kengen.utils;

import android.app.Activity;
import android.content.Intent;

import com.google.gms.ads.AdLoader;

public class uiController {

    public static void onBackPressed(Activity activity) {
        AdLoader.finishWithAd(activity);
    }

    public static void showExit(Activity activity) {
       // AdLoader.showExit(activity);
    }

    public static void gotoActivity(Activity activity, Class<?> activityClass, boolean isAds, boolean isFinish) {
        sendIntent(activity, new Intent(activity, activityClass), isAds, isFinish);
    }

    public static void gotoIntent(Activity activity, Intent intent, boolean isAds, boolean isFinish) {
        sendIntent(activity, intent, isAds, isFinish);
    }

    public static void sendIntent(Activity activity, Intent intent, boolean isAds, boolean isFinish) {
        if (isAds) {
            if (isFinish) {
                AdLoader.startActivityWithFinishAd(activity, intent);
            } else {
                AdLoader.startActivityWithAd(activity, intent);
            }
        } else {
            activity.startActivity(intent);
            if (isFinish) activity.finish();
        }
    }
}