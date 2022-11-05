package com.google.gms.ads;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.preference.PowerPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ListNativeAds {

    private static ArrayList<NativeAd> gNativeAd = new ArrayList<>();


    private boolean isNativeAdLoaded = false;

    public void loadNativeAds(Activity activity) {
        final String nativeAdstr = PowerPreference.getDefaultFile().getString(AdUtils.NATIVEID, "123");

        if (!isNativeAdLoaded && !nativeAdstr.equalsIgnoreCase("") && PowerPreference.getDefaultFile().getBoolean(AdUtils.GoogleAdsOnOff)) {
            if (gNativeAd.size() >= 5) {
                Collections.shuffle(gNativeAd);
                return;
            }


            AdLoader adLoader = new AdLoader.Builder(activity, nativeAdstr).forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                @Override
                public void onNativeAdLoaded(@NonNull NativeAd natives) {
                    gNativeAd.add(natives);
                    isNativeAdLoaded = true;
                }
            }).withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(LoadAdError errorCode) {
                    AdUtils.showLog(errorCode.toString());
                    isNativeAdLoaded = false;
                }
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    AdUtils.closeGoogleAds();
                }
            }).withNativeAdOptions(new NativeAdOptions.Builder()
                    .setVideoOptions(new VideoOptions.Builder()
                            .setStartMuted(true)
                            .build())
                    .build()).build();


            adLoader.loadAds(new AdRequest.Builder().build(), 5);
        }
    }


    public void showListNativeAds(Activity activity, LinearLayout adLayout, TextView adSpace) {

        LinearLayout adView;

        if (!PowerPreference.getDefaultFile().getBoolean(AdUtils.GoogleAdsOnOff, false)) {
            showQureka(activity, adLayout, adSpace);
        } else if (gNativeAd.size() > 0) {

            Collections.shuffle(gNativeAd);
            if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneListNative, 0) == 1) {
                adView = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.layout_native_large, null);
            } else if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneListNative, 0) == 2) {
                adView = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.layout_native_mini, null);
            } else {
                adView = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.layout_native_medium, null);
            }

            NativeAd lovalNative = gNativeAd.get(0);

            populateUnifiedNativeAdView(lovalNative, adView.findViewById(R.id.uadview));

            adLayout.removeAllViews();
            adLayout.addView(adView);

            loadNativeAds(activity);

        } else {
            showQureka(activity, adLayout, adSpace);
        }
    }

    public void showQureka(Activity activity, LinearLayout adLayout, TextView adSpace) {
        if (PowerPreference.getDefaultFile().getBoolean(AdUtils.QurekaOnOff, false)) {

            LinearLayout adView;

            if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneListNative, 0) == 1) {
                adView = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.qureka_native_large, null);
            } else if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneListNative, 0) == 2) {
                adView = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.qureka_native_mini, null);
            } else {
                adView = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.qureka_native_medium, null);
            }

            AdUtils.setQureka(activity, AdUtils.QLIST_COUNT, adView.findViewById(R.id.qurekaAds1), adView.findViewById(R.id.qurekaAds), adView.findViewById(R.id.gif_inter_round));

            adLayout.removeAllViews();
            adLayout.addView(adView);

            loadNativeAds(activity);

        } else {
            adLayout.removeAllViews();
            loadNativeAds(activity);
        }
    }


    public void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {

        if (adView.findViewById(R.id.ad_media) != null) {
            MediaView mediaView = adView.findViewById(R.id.ad_media);
            mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            adView.setMediaView(mediaView);

        }
        if (adView.findViewById(R.id.ad_headline) != null)
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));

        if (adView.findViewById(R.id.ad_body) != null)
            adView.setBodyView(adView.findViewById(R.id.ad_body));

        if (adView.findViewById(R.id.ad_call_to_action) != null)
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));

        if (adView.findViewById(R.id.ad_app_icon) != null)
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));

        if (adView.findViewById(R.id.ad_stars) != null)
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));


        if (nativeAd.getStarRating() == null) {
            if (adView.getStarRatingView() != null)
                Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.VISIBLE);
        } else {
            if (adView.getStarRatingView() != null) {
                Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.VISIBLE);
                ((RatingBar) adView.getStarRatingView()).setRating(Float.parseFloat(String.valueOf(nativeAd.getStarRating())));
            }
        }

        if (nativeAd.getHeadline() == null) {
            if (adView.getHeadlineView() != null)
                Objects.requireNonNull(adView.getHeadlineView()).setVisibility(View.GONE);
        } else {
            if (adView.getHeadlineView() != null) {
                Objects.requireNonNull(adView.getHeadlineView()).setVisibility(View.VISIBLE);
                ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
            }
        }

        if (nativeAd.getBody() == null) {
            if (adView.getBodyView() != null)
                Objects.requireNonNull(adView.getBodyView()).setVisibility(View.GONE);
        } else {
            if (adView.getBodyView() != null) {
                Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }
        }

        if (nativeAd.getCallToAction() == null) {
            if (adView.getCallToActionView() != null)
                Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            if (adView.getCallToActionView() != null) {
                Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
                ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }
        }

        if (nativeAd.getIcon() == null) {
            if (adView.getIconView() != null)
                Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
        } else {
            if (adView.getIconView() != null) {
                ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(
                        nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
        }

        adView.setNativeAd(nativeAd);
    }

}
