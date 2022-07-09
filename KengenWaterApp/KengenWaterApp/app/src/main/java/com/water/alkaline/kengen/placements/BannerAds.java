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
    public static AdView adView;
    public static boolean isLoaded = false;

    public void loadBannerAds(Activity activity) {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleAdsOnOff, false) && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleBannerOnOff, false)) {

            final String Ad = PowerPreference.getDefaultFile().getString(Constant.BANNERID, "123");
            adView = new AdView(activity);
            adView.setAdUnitId(Ad);
            adView.setAdSize(AdSize.SMART_BANNER);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    isLoaded = false;
                    adView = null;
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    isLoaded = true;
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    int clickCOunt = PowerPreference.getDefaultFile().getInt(Constant.APP_CLICK_COUNT, 0);
                    PowerPreference.getDefaultFile().putInt(Constant.APP_CLICK_COUNT, clickCOunt + 1);
                    int clickCOunt2 = PowerPreference.getDefaultFile().getInt(Constant.APP_CLICK_COUNT, 0);

                    if (clickCOunt2 >= PowerPreference.getDefaultFile().getInt(Constant.AD_CLICK_COUNT, 3)) {
                        PowerPreference.getDefaultFile().putBoolean(Constant.GoogleAdsOnOff,false );
                    }
                }
            });
            adView.loadAd(new AdRequest.Builder().build());
        }
    }

    public void showBannerAds(Activity activity, FrameLayout nativeAd, TextView adSpace) {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, true)) {
            if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleAdsOnOff, false) && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleBannerOnOff, true) &&
                    adView != null && isLoaded) {

                nativeAd.removeAllViews();
                nativeAd.addView(adView);

                adSpace.setVisibility(View.GONE);
                nativeAd.setVisibility(View.VISIBLE);

                loadBannerAds(activity);

            }else{
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
