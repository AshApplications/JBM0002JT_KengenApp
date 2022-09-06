package com.water.alkaline.kengen.placements;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.preference.PowerPreference;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.utils.Constant;

public class MainAds {

    public void loadAds(Activity activity) {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, true)) {

            new InterAds().loadInterAds(activity);
            new BackInterAds().loadInterAds(activity);
            new OpenSplashAds().loadOpenAd(activity);
            new OpenAds().loadOpenAd();

            loadBannerAds(activity);
            loadNativeAds(activity);
            loadListNativeAds(activity);
        }
    }

    // INTER ADS

    public void showSplashInterAds(Activity activity, InterAds.OnAdClosedListener listener) {
        PowerPreference.getDefaultFile().putInt(Constant.APP_INTERVAL_COUNT, 0);
        new InterAds().watchAds(activity, listener);
    }

    public void showInterAds(Activity activity, InterAds.OnAdClosedListener listener) {
        new InterAds().showInterAds(activity, listener);
    }

    public void showBackInterAds(Activity activity, BackInterAds.OnAdClosedListener listener) {
        new BackInterAds().showInterAds(activity, listener);
    }

    // OPEN ADS

    public void showOpenAds(Activity activity, OpenSplashAds.OnAdClosedListener listener) {
        new OpenSplashAds().showOpenAd(activity, listener);
    }

    // BANNER ADS

    public void loadBannerAds(Activity activity) {
        if (PowerPreference.getDefaultFile().getInt(Constant.BannerAdWhichOne, 0) == 0) {
            new BannerAds().loadBannerAds(activity);
        } else {
            new MiniNativeAds().loadNativeAds(activity);
        }
    }

    public void showBannerAds(Activity activity, FrameLayout adLayout, TextView adSpace) {

        if (adLayout == null || adSpace == null) {
            adLayout = activity.findViewById(R.id.adFrameMini);
            adSpace = activity.findViewById(R.id.adSpaceMini);
        }

        if (adLayout == null || adSpace == null)
            return;

        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, true)) {
            if (PowerPreference.getDefaultFile().getInt(Constant.BannerAdWhichOne, 0) == 0) {
                new BannerAds().showBannerAds(activity, adLayout, adSpace);
            } else {
                new MiniNativeAds().showNativeAds(activity, null, adLayout, adSpace);
            }
        } else {
            adLayout.setVisibility(View.GONE);
            adSpace.setVisibility(View.GONE);
        }
    }


    // LARGE NATIVE ADS


    public void loadNativeAds(Activity activity) {
        new LargeNativeAds().loadNativeAds(activity);
    }

    public void showNativeAds(Activity activity, Dialog dialog, FrameLayout adLayout, TextView
            adSpace) {

        if (adLayout == null || adSpace == null) {
            adLayout = getFrameLayout(activity, dialog);
            adSpace = getTextLayout(activity, dialog);
        }

        if (adLayout == null || adSpace == null)
            return;

        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, true)) {
            new LargeNativeAds().showNativeAds(activity, dialog, adLayout, adSpace);
        } else {
            adLayout.setVisibility(View.GONE);
            adSpace.setVisibility(View.GONE);
        }
    }

    public FrameLayout getFrameLayout(Activity activity, Dialog dialog) {
        if (dialog != null) {
            return dialog.findViewById(R.id.adFrameLarge);
        } else {
            return activity.findViewById(R.id.adFrameLarge);
        }
    }


    public TextView getTextLayout(Activity activity, Dialog dialog) {
        if (dialog != null) {
            return dialog.findViewById(R.id.adSpaceLarge);
        } else {
            return activity.findViewById(R.id.adSpaceLarge);
        }
    }


    // LIST NATIVE ADS

    public void loadListNativeAds(Activity activity) {
        if (PowerPreference.getDefaultFile().getInt(Constant.ListNativeWhichOne, 1) < 3) {
            new ListNativeAds().loadNativeAds(activity);
            new ListNativeAds().loadNativeAds(activity);
        }
    }

    public void showListNativeAds(Activity activity, FrameLayout nativeAd, TextView adSpace) {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, true)) {
            new ListNativeAds().showListNativeAds(activity, nativeAd, adSpace);
        } else {
            nativeAd.setVisibility(View.GONE);
            adSpace.setVisibility(View.GONE);
        }
    }

}
