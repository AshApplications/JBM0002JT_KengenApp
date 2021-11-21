package com.water.alkaline.kengen.placements;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.Objects;

public class NativeListAds {

    public static boolean apCheck = false;

    public static int gCount = 0;
    public static int aCount = 0;

    private static ArrayList<NativeAd> gNativeAds = new ArrayList<>();
    public static ArrayList<com.appodeal.ads.NativeAd> apNativeAd = new ArrayList<>();

    public void loadnatives(Activity activity) {
        PowerPreference.getDefaultFile().putInt(Constant.NATIVE_POSITION, -1);
        loadMainNatives(activity);
    }

    public void loadMainNatives(Activity activity) {
        int position = PowerPreference.getDefaultFile().getInt(Constant.NATIVE_POSITION);
        PowerPreference.getDefaultFile().putInt(Constant.NATIVE_POSITION, position + 1);
        int position2 = PowerPreference.getDefaultFile().getInt(Constant.NATIVE_POSITION);

        if (position2 < MyApplication.arrayList.size()) {
            Log.e("TAG", "loadMainNative " + MyApplication.arrayList.get(position2));
            switch (MyApplication.arrayList.get(position2)) {
                case 0:
                    loadGNative(activity);
                    break;
                case 1:
                    loadApNative(activity);
                    break;
                default:
                    loadMainNatives(activity);
                    break;
            }
        } else {
            PowerPreference.getDefaultFile().putInt(Constant.NATIVE_POSITION, -1);
        }
    }


    private void loadGNative(Activity activity) {
        Log.e("TAG", "loadGNatives ");
        final String nativeAdstr = PowerPreference.getDefaultFile().getString(Constant.G_NATIVEID);
        AdLoader.Builder builder = new AdLoader.Builder(activity, nativeAdstr);

        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull NativeAd natives) {
                Log.e("TAG", "loadGNativesAD onNativeAdLoaded");
                gNativeAds.add(natives);
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
                Log.e("TAG", "loadGNativesAD onAdFailedToLoad");
                loadMainNatives(activity);
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                int clickCOunt = PowerPreference.getDefaultFile().getInt(Constant.APP_CLICK_COUNT, 0);
                PowerPreference.getDefaultFile().putInt(Constant.APP_CLICK_COUNT, clickCOunt + 1);
                int clickCOunt2 = PowerPreference.getDefaultFile().getInt(Constant.APP_CLICK_COUNT, 0);

                if (clickCOunt2 >= PowerPreference.getDefaultFile().getInt(Constant.AD_CLICK_COUNT, 3)) {
                    PowerPreference.getDefaultFile().putInt(Constant.QUREKA, 99999);
                }
            }
        }).build();

        adLoader.loadAds(new AdRequest.Builder().build(), 5);

    }

    private void loadApNative(Activity activity) {
        Log.e("TAG", "loadApNatives ");
        apCheck = true;
        Appodeal.cache(activity, Appodeal.NATIVE, 5);
        Appodeal.setNativeCallbacks(new NativeCallbacks() {
            @Override
            public void onNativeLoaded() {
                Log.e("TAG", "loadApNatives onNativeLoaded");
                if (apCheck && apNativeAd.size() < 5) {
                    apCheck = false;
                    try {
                        apNativeAd.addAll(Appodeal.getNativeAds(5));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    PowerPreference.getDefaultFile().putInt(Constant.NATIVE_POSITION, -1);
                }
            }

            @Override
            public void onNativeFailedToLoad() {
                Log.e("TAG", "loadApNatives onAdFailedToLoad ");
                if (apCheck) {
                    apCheck = false;
                    loadMainNatives(activity);
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
                int clickCOunt = PowerPreference.getDefaultFile().getInt(Constant.APP_CLICK_COUNT, 0);
                PowerPreference.getDefaultFile().putInt(Constant.APP_CLICK_COUNT, clickCOunt + 1);
                int clickCOunt2 = PowerPreference.getDefaultFile().getInt(Constant.APP_CLICK_COUNT, 0);

                if (clickCOunt2 >= PowerPreference.getDefaultFile().getInt(Constant.AD_CLICK_COUNT, 3)) {
                    PowerPreference.getDefaultFile().putInt(Constant.QUREKA, 99999);
                }
            }

            @Override
            public void onNativeExpired() {

            }
        });
    }


    public void setBanner(TextView adSpace) {
        if (PowerPreference.getDefaultFile().getInt("nCount", 0) == 7) {
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
            Log.e("TAG", "showMainNatives " + MyApplication.arrayList.get(position2));
            switch (MyApplication.arrayList.get(position2)) {
                case 0:
                    showGNative(activity, nativeAdLayout1, nativeAdLayout2, adSpaceNative);
                    break;
                case 1:
                    showApNative(activity,  nativeAdLayout1, nativeAdLayout2, adSpaceNative);
                    break;
                default:
                    showMainNatives(activity,  nativeAdLayout1, nativeAdLayout2, adSpaceNative);
                    break;
            }
        } else {
            loadnatives(activity);
        }
    }


    private void showGNative(Activity activity, RelativeLayout nativeAdLayout1, RelativeLayout nativeAdLayout2, TextView adSpaceNative) {
        Log.e("TAG", "showGNatives ");
        if (gNativeAds.size() > 0) {

            if (gCount > gNativeAds.size() - 1) {
                gCount = 0;
            }
            NativeAdView adView = nativeAdLayout1.findViewById(R.id.adSpaceNativeGoogle);

            NativeAd lovalNative = gNativeAds.get(gCount);
            gCount++;
            populateUnifiedNativeAdView(lovalNative, adView);

            nativeAdLayout1.setVisibility(View.VISIBLE);
            nativeAdLayout2.setVisibility(View.GONE);

            loadnatives(activity);

        } else {
            showMainNatives(activity, nativeAdLayout1,nativeAdLayout2, adSpaceNative);
        }
    }

    private void showApNative(Activity activity,RelativeLayout nativeAdLayout1, RelativeLayout nativeAdLayout2, TextView adSpaceNative) {
        Log.e("TAG", "showApNatives ");

        if (apNativeAd.size() > 0) {

            if (aCount > apNativeAd.size() - 1) {
                aCount = 0;
            }
            com.appodeal.ads.NativeAdView adView = nativeAdLayout2.findViewById(R.id.adSpaceNativeAppo);

            com.appodeal.ads.NativeAd lovalNative = apNativeAd.get(aCount);
            aCount++;

            populateUnifiedAppoNativeAdView(lovalNative, adView);

            nativeAdLayout2.setVisibility(View.VISIBLE);
            nativeAdLayout1.setVisibility(View.GONE);

            loadnatives(activity);

        } else {
            showMainNatives(activity, nativeAdLayout1,nativeAdLayout2, adSpaceNative);
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

        /*
        if (nativeAd.get() == null) {
            Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);*/
    }


}
