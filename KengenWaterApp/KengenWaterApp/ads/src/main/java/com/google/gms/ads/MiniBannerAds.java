package com.google.gms.ads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.preference.PowerPreference;

public class MiniBannerAds {

    @SuppressLint("StaticFieldLeak")
    public static Activity mActivity;

    private AdSize getAdSize(Activity activity,LinearLayout adLayout) {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adLayout.getWidth();

        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    public void loadBannerAds(Activity activity, LinearLayout adLayout, TextView adSpace) {
        final String Ad = PowerPreference.getDefaultFile().getString(AdUtils.BANNERID, "123");

        if (!PowerPreference.getDefaultFile().getBoolean(AdUtils.GoogleAdsOnOff, false)) {
            showQureka(activity, adLayout, adSpace);
        } else if (!Ad.equalsIgnoreCase("")) {

            AdView adView = new AdView(activity);
            adView.setAdSize(getAdSize(activity,adLayout));
            adView.setAdUnitId(Ad);

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();

                    adSpace.getLayoutParams().height = AdSize.SMART_BANNER.getHeightInPixels(activity);

                    adLayout.removeAllViews();
                    adLayout.addView(adView);
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    AdUtils.closeGoogleAds();
                }
                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    AdUtils.showLog(loadAdError.toString());
                    showQureka(activity, adLayout, adSpace);
                }
            });

            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        } else {
            showQureka(activity, adLayout, adSpace);
        }

    }

    public void showQureka(Activity activity, LinearLayout adLayout, TextView adSpace) {
        if (PowerPreference.getDefaultFile().getBoolean(AdUtils.QurekaOnOff, false)) {
            LinearLayout adView = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.qureka_native_tiny, null);
            AdUtils.setQureka(activity, AdUtils.QMINI_COUNT, adView.findViewById(R.id.qurekaAds1), adView.findViewById(R.id.qurekaAds), adView.findViewById(R.id.gif_inter_round));
            adView.setOnClickListener(v->AdUtils.gotoAds(activity));
            adLayout.removeAllViews();
            adLayout.addView(adView);
        } else {
            adLayout.removeAllViews();
        }
    }
}
