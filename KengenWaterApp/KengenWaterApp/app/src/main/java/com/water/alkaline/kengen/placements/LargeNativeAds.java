package com.water.alkaline.kengen.placements;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.utils.Constant;

import java.util.ArrayList;
import java.util.Objects;

public class LargeNativeAds {

    private static ArrayList<NativeAd> gNativeAd = new ArrayList<>();
    private static String ad_type;

    public void loadNativeAds(Activity activity) {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleAdsOnOff, false) && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleLargeNativeOnOff, false)) {
            final String nativeAdstr = PowerPreference.getDefaultFile().getString(Constant.NATIVEID, "123");

            AdLoader.Builder builder = new AdLoader.Builder(activity, nativeAdstr);
            builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                @Override
                public void onNativeAdLoaded(@NonNull NativeAd natives) {
                    gNativeAd.clear();
                    gNativeAd.add(natives);
                }
            });

            VideoOptions videoOptions = new VideoOptions.Builder()
                    .setStartMuted(true)
                    .build();

            NativeAdOptions adOptions = new NativeAdOptions.Builder()
                    .setVideoOptions(videoOptions)
                    .build();

            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(LoadAdError errorCode) {
                    Constant.showLog("loadNativeAds failed" + errorCode.toString());
                    gNativeAd.clear();
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
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }


    public FrameLayout getFrameLayout(Activity activity, Dialog dialog) {
        if (dialog != null) {
            return dialog.findViewById(R.id.frameNativeLarge);
        } else {
            return activity.findViewById(R.id.frameNativeLarge);
        }
    }


    public TextView getTextLayout(Activity activity, Dialog dialog) {
        if (dialog != null) {
            return dialog.findViewById(R.id.adSpaceLarge);
        } else {
            return activity.findViewById(R.id.adSpaceLarge);
        }
    }


    public void showNativeAds(Activity activity, Dialog dialog) {

        FrameLayout nativeAdLayout = getFrameLayout(activity, dialog);
        TextView adSpace = getTextLayout(activity, dialog);

        LinearLayout adView = null;

        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, true)) {

            if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleAdsOnOff, false) && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleLargeNativeOnOff, true) && gNativeAd.size() > 0) {

                adView = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.ads_native_large, null);

                NativeAd lovalNative = gNativeAd.get(0);

                populateUnifiedNativeAdView(lovalNative, adView.findViewById(R.id.uadview));

                nativeAdLayout.removeAllViews();
                nativeAdLayout.addView(adView);

                adSpace.setVisibility(View.GONE);
                nativeAdLayout.setVisibility(View.VISIBLE);

                loadNativeAds(activity);

            } else {

                loadNativeAds(activity);

                if (PowerPreference.getDefaultFile().getBoolean(Constant.QurekaOnOff, true) && PowerPreference.getDefaultFile().getBoolean(Constant.QurekaLargeNativeOnOff, true)) {

                    adView = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.qureka_native_large, null);

                    ImageView imageViewMain = adView.findViewById(R.id.qurekaAds1);
                    ImageView imageViewBG = adView.findViewById(R.id.qurekaAds);
                    ImageView imageViewGif = adView.findViewById(R.id.gif_inter_round);

                    Constant.setQureka(activity, imageViewMain, imageViewBG, imageViewGif, Constant.QLARGE_COUNT);

                    adView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Constant.gotoAds(activity);
                        }
                    });

                    nativeAdLayout.removeAllViews();
                    nativeAdLayout.addView(adView);

                    adSpace.setVisibility(View.GONE);
                    nativeAdLayout.setVisibility(View.VISIBLE);

                } else {
                    nativeAdLayout.setVisibility(View.GONE);
                    adSpace.setVisibility(View.GONE);
                }
            }
        } else {
            nativeAdLayout.setVisibility(View.GONE);
            adSpace.setVisibility(View.GONE);
        }
    }

    public void showNativeAds(Activity activity, FrameLayout nativeAd, TextView adSpace) {

        LinearLayout adView = null;

        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, true)) {

            if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleAdsOnOff,false ) && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleLargeNativeOnOff, true) && gNativeAd.size() > 0) {

                adView = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.ads_native_large, null);

                NativeAd lovalNative = gNativeAd.get(0);

                populateUnifiedNativeAdView(lovalNative, adView.findViewById(R.id.uadview));

                nativeAd.removeAllViews();
                nativeAd.addView(adView);

                adSpace.setVisibility(View.GONE);
                nativeAd.setVisibility(View.VISIBLE);

                loadNativeAds(activity);

            } else {

                loadNativeAds(activity);

                if (PowerPreference.getDefaultFile().getBoolean(Constant.QurekaOnOff, true) && PowerPreference.getDefaultFile().getBoolean(Constant.QurekaLargeNativeOnOff, true)) {

                    adView = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.qureka_native_large, null);

                    ImageView imageViewMain = adView.findViewById(R.id.qurekaAds1);
                    ImageView imageViewBG = adView.findViewById(R.id.qurekaAds);
                    ImageView imageViewGif = adView.findViewById(R.id.gif_inter_round);

                    Constant.setQureka(activity, imageViewMain, imageViewBG, imageViewGif, Constant.QLARGE_COUNT);

                    adView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Constant.gotoAds(activity);
                        }
                    });

                    nativeAd.removeAllViews();
                    nativeAd.addView(adView);

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
                Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.GONE);
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
