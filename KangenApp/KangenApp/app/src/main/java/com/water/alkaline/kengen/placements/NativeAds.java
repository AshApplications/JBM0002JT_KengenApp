package com.water.alkaline.kengen.placements;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.NativeCallbacks;
import com.appodeal.ads.NativeMediaView;
import com.bumptech.glide.Glide;
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
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.utils.Constant;

import java.util.Objects;

public class NativeAds {

    public static boolean apCheck = false;
    public static boolean apCheck2 = false;
    public static boolean apCheck3 = false;

    private static NativeAd gNativeAd;
    public static com.appodeal.ads.NativeAd apNativeAd;

    public void loadnative(Activity activity) {
        PowerPreference.getDefaultFile().putInt(Constant.NATIVE_POSITION, -1);
        loadMainNative(activity);
    }

    public void loadMainNative(Activity activity) {
        int position = PowerPreference.getDefaultFile().getInt(Constant.NATIVE_POSITION);
        PowerPreference.getDefaultFile().putInt(Constant.NATIVE_POSITION, position + 1);
        int position2 = PowerPreference.getDefaultFile().getInt(Constant.NATIVE_POSITION);

        if (position2 < MyApplication.arrayList.size()) {
            switch (MyApplication.arrayList.get(position2)) {
                case 0:
                    loadGNative(activity);
                    break;
                case 1:
                    loadApNative(activity);
                    break;
                default:
                    loadMainNative(activity);
                    break;
            }
        } else {
            PowerPreference.getDefaultFile().putInt(Constant.NATIVE_POSITION, -1);
        }
    }


    private void loadGNative(Activity activity) {
        final String nativeAdstr = PowerPreference.getDefaultFile().getString(Constant.G_NATIVEID);
        AdLoader.Builder builder = new AdLoader.Builder(activity, nativeAdstr);

        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull NativeAd natives) {
                gNativeAd = natives;
                PowerPreference.getDefaultFile().putInt(Constant.NATIVE_POSITION, -1);
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
                gNativeAd = null;
                loadMainNative(activity);
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }

    private void loadApNative(Activity activity) {
        apCheck = true;
        apCheck2 = true;
        Appodeal.cache(activity, Appodeal.NATIVE, 3);
        Appodeal.setNativeCallbacks(new NativeCallbacks() {
            @Override
            public void onNativeLoaded() {
                if (apCheck) {
                    apCheck = false;
                    apNativeAd = Appodeal.getNativeAds(1).get(0);
                    PowerPreference.getDefaultFile().putInt(Constant.NATIVE_POSITION, -1);
                }
            }

            @Override
            public void onNativeFailedToLoad() {
                 if (apCheck2) {
                    apCheck2 = false;
                    apNativeAd = null;
                    loadMainNative(activity);
                }
            }

            @Override
            public void onNativeShown(com.appodeal.ads.NativeAd nativeAd) {

            }

            @Override
            public void onNativeShowFailed(com.appodeal.ads.NativeAd nativeAd) {

            }

            @Override
            public void onNativeClicked(com.appodeal.ads.NativeAd nativeAd) {

            }

            @Override
            public void onNativeExpired() {

            }
        });
    }


    public void shownative(Activity activity, Dialog dialog) {

        PowerPreference.getDefaultFile().putInt(Constant.NATIVE_POSITION, -1);
        showMainNative(activity, dialog);
    }


    public void showMainNative(Activity activity, Dialog dialog) {

        int position = PowerPreference.getDefaultFile().getInt(Constant.NATIVE_POSITION);
        PowerPreference.getDefaultFile().putInt(Constant.NATIVE_POSITION, position + 1);
        int position2 = PowerPreference.getDefaultFile().getInt(Constant.NATIVE_POSITION);

        if (position2 < MyApplication.arrayList.size()) {
            switch (MyApplication.arrayList.get(position2)) {
                case 0:
                    showGNative(activity, dialog);
                    break;
                case 1:
                    showApNative(activity, dialog);
                    break;
                default:
                    showMainNative(activity, dialog);
                    break;
            }
        } else {
            loadnative(activity);
        }
    }


    private void showGNative(Activity activity, Dialog dialog) {

        if (gNativeAd != null) {

            NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_native_google, null);

            FrameLayout nativeAdLayout;
            TextView adSpace;

            if (dialog != null) {
                nativeAdLayout = dialog.findViewById(R.id.nativeAd);
                adSpace = dialog.findViewById(R.id.adSpaceNative);
            } else {
                nativeAdLayout = activity.findViewById(R.id.nativeAd);
                adSpace = activity.findViewById(R.id.adSpaceNative);
            }

            NativeAd lovalNative = gNativeAd;
            populateUnifiedNativeAdView(lovalNative, adView);

            nativeAdLayout.removeAllViews();
            nativeAdLayout.addView(adView);

            adSpace.setVisibility(View.GONE);

            loadnative(activity);

        } else {
            showMainNative(activity, dialog);
        }
    }


    private void showApNative(Activity activity, Dialog dialog) {

        if (apNativeAd != null) {

            com.appodeal.ads.NativeAdView adView = (com.appodeal.ads.NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_native_appo, null);
            FrameLayout nativeAdLayout;
            TextView adSpace;

            if (dialog != null) {
                nativeAdLayout = dialog.findViewById(R.id.nativeAd);
                adSpace = dialog.findViewById(R.id.adSpaceNative);
            } else {
                nativeAdLayout = activity.findViewById(R.id.nativeAd);
                adSpace = activity.findViewById(R.id.adSpaceNative);
            }

            populateUnifiedAppoNativeAdView(apNativeAd, adView);

            nativeAdLayout.removeAllViews();
            nativeAdLayout.addView(adView);

            adSpace.setVisibility(View.GONE);

            loadnative(activity);

        } else {
            showMainNative(activity, dialog);
        }
    }


    public void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setPriceView(adView.findViewById(R.id.ad_price));

        if (nativeAd.getHeadline() != null)
            ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());

        if (nativeAd.getStarRating() != null)
            ((RatingBar) adView.findViewById(R.id.ad_stars)).setRating(Float.parseFloat(nativeAd.getStarRating().toString()));

        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getStore() == null) {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }
        if (nativeAd.getPrice() == null) {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getIcon() == null) {
            Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
    }


    public void populateUnifiedAppoNativeAdView(com.appodeal.ads.NativeAd nativeAd, com.appodeal.ads.NativeAdView adView) {

        adView.setNativeMediaView((NativeMediaView) adView.findViewById(R.id.ad_media));
        adView.setTitleView(adView.findViewById(R.id.ad_headline));
        adView.setDescriptionView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setNativeIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setRatingView(adView.findViewById(R.id.ad_stars));
        adView.setProviderView(adView.findViewById(R.id.ad_store));
        adView.registerView(nativeAd);

        ((RatingBar) adView.getRatingView()).setRating(nativeAd.getRating());

        if (nativeAd.getTitle() != null)
            ((TextView) Objects.requireNonNull(adView.getTitleView())).setText(nativeAd.getTitle());

        if (nativeAd.getDescription() == null) {
            Objects.requireNonNull(adView.getDescriptionView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getDescriptionView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getDescriptionView()).setText(nativeAd.getDescription());
        }

        if (nativeAd.getAdProvider() == null) {
            Objects.requireNonNull(adView.getProviderView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getProviderView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getProviderView()).setText(nativeAd.getAdProvider());
        }

        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
    }


    public void setBanner(TextView adSpace) {
        if (PowerPreference.getDefaultFile().getInt("nCount", 0) >= 7) {
            PowerPreference.getDefaultFile().putInt("nCount", 0);
            setBanner(adSpace);
        } else {
            adSpace.setBackgroundResource(Constant.adNAtives[PowerPreference.getDefaultFile().getInt("nCount", 0)]);
            int top = PowerPreference.getDefaultFile().getInt("nCount", 0) + 1;
            PowerPreference.getDefaultFile().putInt("nCount", top);
        }
    }

    public void shownatives(Activity activity, RelativeLayout nativeAdLayout1, RelativeLayout nativeAdLayout2, TextView adSpaceNative) {

        if (PowerPreference.getDefaultFile().getInt(Constant.QUREKA, 5) > 0) {
            nativeAdLayout1.setVisibility(View.GONE);
            nativeAdLayout2.setVisibility(View.GONE);
            adSpaceNative.setVisibility(View.GONE);
            return;
        }

        PowerPreference.getDefaultFile().putInt(Constant.NATIVE_POSITION, -1);
        setBanner(adSpaceNative);
        adSpaceNative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.gotoAds(activity);
            }
        });
         showMainNatives(activity, nativeAdLayout1, nativeAdLayout2, adSpaceNative);
    }

    public void showMainNatives(Activity activity, RelativeLayout nativeAdLayout1, RelativeLayout nativeAdLayout2, TextView adSpaceNative) {

        int position = PowerPreference.getDefaultFile().getInt(Constant.NATIVE_POSITION);
        PowerPreference.getDefaultFile().putInt(Constant.NATIVE_POSITION, position + 1);
        int position2 = PowerPreference.getDefaultFile().getInt(Constant.NATIVE_POSITION);

        if (position2 < MyApplication.arrayList.size()) {
            switch (MyApplication.arrayList.get(position2)) {
                case 0:
                    showGNatives(activity, nativeAdLayout1, nativeAdLayout2, adSpaceNative);
                    break;
                case 1:
                    showApNatives(activity, nativeAdLayout1, nativeAdLayout2, adSpaceNative);
                    break;
                default:
                    showMainNatives(activity, nativeAdLayout1, nativeAdLayout2, adSpaceNative);
                    break;
            }
        } else {
            loadnative(activity);
        }
    }


    private void showGNatives(Activity activity, RelativeLayout nativeAdLayout1, RelativeLayout nativeAdLayout2, TextView adSpaceNative) {
        if (gNativeAd != null) {

            NativeAdView adView = nativeAdLayout1.findViewById(R.id.adSpaceNativeGoogle);

            NativeAd lovalNative = gNativeAd;
            populateUnifiedNativeAdView(lovalNative, adView);

            nativeAdLayout1.setVisibility(View.VISIBLE);
            nativeAdLayout2.setVisibility(View.GONE);
            adSpaceNative.setVisibility(View.GONE);

            loadnative(activity);

        } else {
            showMainNatives(activity, nativeAdLayout1, nativeAdLayout2, adSpaceNative);
        }
    }

    private void showApNatives(Activity activity, RelativeLayout nativeAdLayout1, RelativeLayout nativeAdLayout2, TextView adSpaceNative) {

        if (apNativeAd != null) {

            com.appodeal.ads.NativeAdView adView = nativeAdLayout2.findViewById(R.id.adSpaceNativeAppo);

            com.appodeal.ads.NativeAd lovalNative = apNativeAd;
            populateUnifiedAppoNativeAdView(lovalNative, adView);

            nativeAdLayout2.setVisibility(View.VISIBLE);
            nativeAdLayout1.setVisibility(View.GONE);
            adSpaceNative.setVisibility(View.GONE);

            loadnative(activity);

        } else {
            showMainNatives(activity, nativeAdLayout1, nativeAdLayout2, adSpaceNative);
        }
    }

}
