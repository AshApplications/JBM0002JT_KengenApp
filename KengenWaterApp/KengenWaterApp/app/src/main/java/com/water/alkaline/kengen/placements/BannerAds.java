package com.water.alkaline.kengen.placements;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.utils.Constant;


public class BannerAds {
    public static AdView adViewMain;
    public static AdView adViewOld;
    public static boolean isLoading = false;

    public void loadBannerAds(Activity activity) {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleAdsOnOff, false) && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleBannerOnOff, false)) {

            isLoading = true;

            final String Ad = PowerPreference.getDefaultFile().getString(Constant.BANNERID, "123");
            adViewMain = new AdView(activity);
            adViewMain.setAdUnitId(Ad);
            adViewMain.setAdSize(AdSize.SMART_BANNER);
            adViewMain.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    isLoading = false;
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    isLoading = false;
                    adViewOld = adViewMain;
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    int clickCOunt = PowerPreference.getDefaultFile().getInt(Constant.APP_CLICK_COUNT, 0);
                    PowerPreference.getDefaultFile().putInt(Constant.APP_CLICK_COUNT, clickCOunt + 1);
                    int clickCOunt2 = PowerPreference.getDefaultFile().getInt(Constant.APP_CLICK_COUNT, 0);

                    if (clickCOunt2 >= PowerPreference.getDefaultFile().getInt(Constant.AD_CLICK_COUNT, 3)) {
                        PowerPreference.getDefaultFile().putBoolean(Constant.GoogleAdsOnOff, false);
                    }
                }
            });
            adViewMain.loadAd(new AdRequest.Builder().build());
        }
    }


    public void showBannerAds(Activity activity, FrameLayout nativeAd, TextView adSpace) {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, true)) {

            if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleAdsOnOff, true) && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleBannerOnOff, true) && adViewOld != null) {

                if (adViewOld.getParent() != null) {
                    ((FrameLayout) adViewOld.getParent()).removeAllViews();
                }

                nativeAd.removeAllViews();
                nativeAd.addView(adViewOld);

                adSpace.setVisibility(View.GONE);
                nativeAd.setVisibility(View.VISIBLE);


                if (!isLoading)
                    loadBannerAds(activity);

            } else {

                if (!isLoading)
                    loadBannerAds(activity);

                if (PowerPreference.getDefaultFile().getBoolean(Constant.QurekaOnOff, true) && PowerPreference.getDefaultFile().getBoolean(Constant.QurekaBannerOnOff, true)) {

                    LinearLayout inflate = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.qureka_banner, null);

                    ImageView imageViewMain = inflate.findViewById(R.id.imageView);
                    Constant.setQurekaBanner(activity, imageViewMain, Constant.QBANNER_COUNT);
                    inflate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Constant.gotoAds(activity);
                        }
                    });

                    nativeAd.removeAllViews();
                    nativeAd.addView(inflate);

                    adSpace.setVisibility(View.GONE);
                    nativeAd.setVisibility(View.VISIBLE);

                } else {
                    nativeAd.setVisibility(View.GONE);
                    adSpace.setVisibility(View.GONE);
                }

            }
        } else {
            nativeAd.setVisibility(View.GONE);
            adSpace.setVisibility(View.GONE);
        }
    }
}
