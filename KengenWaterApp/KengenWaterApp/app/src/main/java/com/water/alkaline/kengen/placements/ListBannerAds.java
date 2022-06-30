package com.water.alkaline.kengen.placements;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.preference.PowerPreference;
import com.water.alkaline.kengen.utils.Constant;

public class ListBannerAds {

    public void showBannerAds(Activity activity, FrameLayout nativeAd, TextView adSpace) {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, true)) {
            if (PowerPreference.getDefaultFile().getInt(Constant.BannerAdWhichOne, 0) == 0) {
                new BannerAds().showBannerAds(activity, nativeAd, adSpace);
            } else {
                new MiniNativeAds().showNativeAds(activity, nativeAd, adSpace);
            }
        } else {
            nativeAd.setVisibility(View.GONE);
            adSpace.setVisibility(View.GONE);
        }
    }

}
