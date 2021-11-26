package com.water.alkaline.kengen.placements;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.BannerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.ironsource.mediationsdk.G;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.utils.Constant;
import com.yandex.metrica.impl.ob.V;

import java.util.HashMap;

public class BannerAds {
    
    public static boolean isAppShow = false;

    public static boolean ApCheck = false;
    public static boolean ApCheck2 = false;

    public static boolean adCheck = false;

    public static ViewGroup parentview;

    private static boolean isGbannerloaded;
    private static AdView gBannerAd;

    private static boolean isApbannerloaded;
    private static com.appodeal.ads.BannerView apBannnerAd;

    public void loadBanner(Activity activity) {
        PowerPreference.getDefaultFile().putInt(Constant.BANNER_POSITION, -1);
        loadMainBanner(activity);
    }

    public void loadMainBanner(Activity activity) {
        int position = PowerPreference.getDefaultFile().getInt(Constant.BANNER_POSITION);
        PowerPreference.getDefaultFile().putInt(Constant.BANNER_POSITION, position + 1);
        int position2 = PowerPreference.getDefaultFile().getInt(Constant.BANNER_POSITION);

        if (position2 < MyApplication.arrayList.size()) {
            switch (MyApplication.arrayList.get(position2)) {
                case 0:
                    loadGBanner(activity);
                    break;
                case 1:
                    loadApBanner(activity);
                    break;
                default:
                    loadMainBanner(activity);
                    break;
            }
        } else {
            PowerPreference.getDefaultFile().putInt(Constant.BANNER_POSITION, -1);
        }
    }

    public void loadGBanner(Activity activity) {
        isGbannerloaded = false;
        final String bannerAd = PowerPreference.getDefaultFile().getString(Constant.G_BANNERID);

        gBannerAd = new AdView(activity);
        gBannerAd.setAdSize(AdSize.BANNER);
        gBannerAd.setAdUnitId(bannerAd);

        AdRequest adRequest = new AdRequest.Builder().build();
        gBannerAd.loadAd(adRequest);

        gBannerAd.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                isGbannerloaded = false;
                loadMainBanner(activity);
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

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                isGbannerloaded = true;
                PowerPreference.getDefaultFile().putInt(Constant.BANNER_POSITION, -1);
            }
        });
    }


    public void loadApBanner(Activity activity) {

        isApbannerloaded = false;
        ApCheck = true;
        ApCheck2 = true;

        Appodeal.setSmartBanners(true);
        Appodeal.cache(activity, Appodeal.BANNER_VIEW);
        Appodeal.setBannerCallbacks(new BannerCallbacks() {
            @Override
            public void onBannerLoaded(int height, boolean isPrecache) {

                if (ApCheck) {
                    ApCheck = false;
                    apBannnerAd = Appodeal.getBannerView(activity);
                    isApbannerloaded = true;
                    PowerPreference.getDefaultFile().putInt(Constant.BANNER_POSITION, -1);
                }
            }

            @Override
            public void onBannerFailedToLoad() {
                if (ApCheck2) {
                    ApCheck2 = false;
                    isApbannerloaded = false;
                    loadMainBanner(activity);
                }
            }

            @Override
            public void onBannerShown() {

            }

            @Override
            public void onBannerShowFailed() {

            }

            @Override
            public void onBannerClicked() {

                int clickCOunt = PowerPreference.getDefaultFile().getInt(Constant.APP_CLICK_COUNT, 0);
                PowerPreference.getDefaultFile().putInt(Constant.APP_CLICK_COUNT, clickCOunt + 1);
                int clickCOunt2 = PowerPreference.getDefaultFile().getInt(Constant.APP_CLICK_COUNT, 0);

                if (clickCOunt2 >= PowerPreference.getDefaultFile().getInt(Constant.AD_CLICK_COUNT, 3)) {
                    PowerPreference.getDefaultFile().putInt(Constant.QUREKA, 99999);
                }

            }

            @Override
            public void onBannerExpired() {

            }
        });
    }

    public void showBanner(Activity activity) {

        final TextView adSpace = activity.findViewById(R.id.adSpaceBanner);
        final RelativeLayout adContainer = activity.findViewById(R.id.adBanner);

        if (adContainer.getChildCount() > 0)
            return;

        if (PowerPreference.getDefaultFile().getInt(Constant.QUREKA, 5) > 0) {
            adContainer.setVisibility(View.GONE);
            adSpace.setVisibility(View.GONE);
            return;
        }

        if (adCheck) {
            adCheck = false;
            return;
        }

        setBanner(adSpace);
        adSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.gotoAds(activity);
            }
        });

        PowerPreference.getDefaultFile().putInt(Constant.BANNER_POSITION, -1);
        showMainBanner(activity, adContainer, adSpace);

    }

    public void setBanner(TextView adSpace) {
        if (PowerPreference.getDefaultFile().getInt("qCount", 0) == 7) {
            PowerPreference.getDefaultFile().putInt("qCount", 0);
            setBanner(adSpace);
        } else {
            adSpace.setBackgroundResource(Constant.adBanners[PowerPreference.getDefaultFile().getInt("qCount", 0)]);
            int top = PowerPreference.getDefaultFile().getInt("qCount", 0) + 1;
            PowerPreference.getDefaultFile().putInt("qCount", top);
        }
    }

    public void showMainBanner(Activity activity, RelativeLayout adContainer, TextView adSpace) {

        int position = PowerPreference.getDefaultFile().getInt(Constant.BANNER_POSITION);
        PowerPreference.getDefaultFile().putInt(Constant.BANNER_POSITION, position + 1);
        int position2 = PowerPreference.getDefaultFile().getInt(Constant.BANNER_POSITION);

        if (position2 < MyApplication.arrayList.size()) {
            switch (MyApplication.arrayList.get(position2)) {
                case 0:
                    showGBanner(activity, adContainer, adSpace);
                    break;
                case 1:
                    showApBanner(activity, adContainer, adSpace);
                    break;
                default:
                    showMainBanner(activity, adContainer, adSpace);
                    break;
            }
        } else {
            loadBanner(activity);
        }
    }


    public void showGBanner(Activity activity, RelativeLayout adContainer, TextView adSpace) {
        if (isGbannerloaded) {

            if (gBannerAd != null) {

                if (parentview != null)
                    parentview.removeAllViews();


                adContainer.removeAllViews();
                adContainer.addView(gBannerAd);
                parentview = adContainer;

                if (isAppShow) {
                    isAppShow = false;
                    Appodeal.hide(activity, Appodeal.BANNER_VIEW);
                }

                adContainer.setVisibility(View.VISIBLE);
            }

            loadBanner(activity);

        } else {
            showMainBanner(activity, adContainer, adSpace);
        }
    }

    public void showApBanner(Activity activity, RelativeLayout adContainer, TextView adSpace) {
        if (isApbannerloaded) {

            if (apBannnerAd != null) {

                if (parentview != null)
                    parentview.removeAllViews();

                adContainer.removeAllViews();
                adContainer.addView(apBannnerAd);
                parentview = adContainer;

                isAppShow = true;
                Appodeal.show(activity, Appodeal.BANNER_VIEW);
                adContainer.setVisibility(View.VISIBLE);

            }

            loadBanner(activity);

        } else {
            showMainBanner(activity, adContainer, adSpace);
        }
    }
}
