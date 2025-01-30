package com.google.gms.ads;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAppOpenAd;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.preference.PowerPreference;

import java.util.Date;

public class AppOpenManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {

    public static boolean appIsShowingAd = false;
    public boolean isAdLoading = false;
    public AppOpenAd appOpenAd = null;
    public MaxAppOpenAd maxAppOpenAd = null;
    public Activity appCurrentActivity;

    public AppOpenManager() {
        MyApp.getInstance().registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    public void fetchAd() {

        if (isAdAvailable() || isAdLoading) {
            return;
        }
        if (AdLoader.getFailedCountAppOpen() < MyApp.getAdModel().getAdsAppOpenFailedCount() && MyApp.getAdModel().getAdsAppOpen().equalsIgnoreCase(AdLoader.AD_GOOGLE) && MyApp.getAdModel().getAdsOnOff().equalsIgnoreCase("Yes")) {
            isAdLoading = true;
            AdRequest request = getAdRequest();
            AdLoader.log("APPOPEN -> AD REQUEST");
            AppOpenAd.load(MyApp.getInstance(), MyApp.getAdModel().getAdsAppOpenId(), request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                    super.onAdLoaded(appOpenAd);
                    AdLoader.log("APPOPEN -> AD LOADED");
                    AppOpenManager.this.appOpenAd = appOpenAd;
                    AdLoader.resetFailedCountAppOpen();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    isAdLoading = false;
                    AppOpenManager.this.appOpenAd = null;
                    AdLoader.increaseFailedCountAppOpen();
                    AdLoader.log("APPOPEN -> AD FAILED (" + AdLoader.getFailedCountAppOpen() + " of " + MyApp.getAdModel().getAdsAppOpenFailedCount() + ")\nKEY: " + MyApp.getAdModel().getAdsAppOpenId() + "ERROR: " + loadAdError.getMessage());
                }
            });
        } else if (PowerPreference.getDefaultFile().getBoolean(AdUtils.isAppLovinLoaded, false) && AdLoader.getFailedCountAppOpen() < MyApp.getAdModel().getAdsAppOpenFailedCount() && MyApp.getAdModel().getAdsAppOpen().equalsIgnoreCase(AdLoader.AD_APPLOVIN) && MyApp.getAdModel().getAdsOnOff().equalsIgnoreCase("Yes")) {
            isAdLoading = true;
            MaxAppOpenAd maxAppOpenAd = new MaxAppOpenAd(MyApp.getAdModel().getAdsAppOpenId(), appCurrentActivity);
            maxAppOpenAd.setListener(new MaxAdListener() {
                @Override
                public void onAdLoaded(@NonNull MaxAd maxAd) {
                    AdLoader.log("APPOPEN -> AD LOADED");
                    AppOpenManager.this.maxAppOpenAd = maxAppOpenAd;
                    AdLoader.resetFailedCountAppOpen();
                }

                @Override
                public void onAdDisplayed(@NonNull MaxAd maxAd) {

                }

                @Override
                public void onAdHidden(@NonNull MaxAd maxAd) {

                }

                @Override
                public void onAdClicked(@NonNull MaxAd maxAd) {

                }

                @Override
                public void onAdLoadFailed(@NonNull String s, @NonNull MaxError maxError) {
                    AppOpenManager.this.maxAppOpenAd = null;
                    AdLoader.increaseFailedCountAppOpen();
                    AdLoader.log("APPOPEN -> AD FAILED (" + AdLoader.getFailedCountAppOpen() + " of " + MyApp.getAdModel().getAdsAppOpenFailedCount() + ")\nKEY: " + MyApp.getAdModel().getAdsAppOpenId() + "ERROR: " + maxError.getMessage());

                }

                @Override
                public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {

                }
            });
            AdLoader.log("APPOPEN -> AD REQUEST");
            maxAppOpenAd.loadAd();
        } else {
            AdLoader.log("APPOPEN -> FAILED COUNTER IS " + MyApp.getAdModel().getAdsAppOpenFailedCount());
        }
    }


    public AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    public boolean isAdAvailable() {
        if (MyApp.getAdModel().getAdsAppOpen().equalsIgnoreCase(AdLoader.AD_GOOGLE)) {
            return appOpenAd != null;
        } else if (MyApp.getAdModel().getAdsAppOpen().equalsIgnoreCase(AdLoader.AD_APPLOVIN)) {
            return maxAppOpenAd != null;
        } else return false;
    }

    public void showAdIfAvailable() {
        if (!AdLoader.getInstance().isInterstitialShowing && !appIsShowingAd && isAdAvailable() && MyApp.getAdModel().getAdsOnOff().equalsIgnoreCase("Yes")) {
            if (MyApp.getAdModel().getAdsAppOpen().equalsIgnoreCase(AdLoader.AD_GOOGLE)) {
                FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        AppOpenManager.this.appOpenAd = null;
                        isAdLoading = false;
                        appIsShowingAd = false;
                        fetchAd();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        AdLoader.getInstance().closeAds();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        appIsShowingAd = true;
                    }
                };

                appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                AdLoader.log("APPOPEN -> AD SHOW");
                appOpenAd.show(appCurrentActivity);
            } else if (PowerPreference.getDefaultFile().getBoolean(AdUtils.isAppLovinLoaded, false) && MyApp.getAdModel().getAdsAppOpen().equalsIgnoreCase(AdLoader.AD_APPLOVIN)) {
                maxAppOpenAd.setListener(new MaxAdViewAdListener() {
                    @Override
                    public void onAdExpanded(@NonNull MaxAd maxAd) {

                    }

                    @Override
                    public void onAdCollapsed(@NonNull MaxAd maxAd) {

                    }

                    @Override
                    public void onAdLoaded(@NonNull MaxAd maxAd) {

                    }

                    @Override
                    public void onAdDisplayed(@NonNull MaxAd maxAd) {
                        appIsShowingAd = true;
                    }

                    @Override
                    public void onAdHidden(@NonNull MaxAd maxAd) {
                        AppOpenManager.this.maxAppOpenAd = null;
                        appIsShowingAd = false;
                        isAdLoading = false;
                        fetchAd();
                    }

                    @Override
                    public void onAdClicked(@NonNull MaxAd maxAd) {
                        AdLoader.getInstance().closeAds();
                    }

                    @Override
                    public void onAdLoadFailed(@NonNull String s, @NonNull MaxError maxError) {

                    }

                    @Override
                    public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {

                    }
                });
                AdLoader.log("APPOPEN -> AD SHOW");
                maxAppOpenAd.showAd();
            }

        } else {
            fetchAd();
        }
    }

    public void showAdIfSplashAvailable(@NonNull final Activity activity, @NonNull MyApp.OnShowAdCompleteListener onShowAdCompleteListener) {
        if (!appIsShowingAd && isAdAvailable() && MyApp.getAdModel().getAdsOnOff().equalsIgnoreCase("Yes")) {
            if (MyApp.getAdModel().getAdsAppOpen().equalsIgnoreCase(AdLoader.AD_GOOGLE)) {
                FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        AppOpenManager.this.appOpenAd = null;
                        appIsShowingAd = false;
                        isAdLoading = false;
                        fetchAd();
                        onShowAdCompleteListener.onShowAdComplete();
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        AdLoader.getInstance().closeAds();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        onShowAdCompleteListener.onShowAdComplete();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        appIsShowingAd = true;
                    }
                };
                appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                AdLoader.log("APPOPEN -> AD SHOW");
                appOpenAd.show(activity);
            } else if (PowerPreference.getDefaultFile().getBoolean(AdUtils.isAppLovinLoaded, false) && MyApp.getAdModel().getAdsAppOpen().equalsIgnoreCase(AdLoader.AD_APPLOVIN)) {
                maxAppOpenAd.setListener(new MaxAdViewAdListener() {
                    @Override
                    public void onAdExpanded(@NonNull MaxAd maxAd) {

                    }

                    @Override
                    public void onAdCollapsed(@NonNull MaxAd maxAd) {

                    }

                    @Override
                    public void onAdLoaded(@NonNull MaxAd maxAd) {

                    }

                    @Override
                    public void onAdDisplayed(@NonNull MaxAd maxAd) {
                        appIsShowingAd = true;
                    }

                    @Override
                    public void onAdHidden(@NonNull MaxAd maxAd) {
                        AppOpenManager.this.maxAppOpenAd = null;
                        appIsShowingAd = false;
                        isAdLoading = false;
                        fetchAd();
                        onShowAdCompleteListener.onShowAdComplete();
                    }

                    @Override
                    public void onAdClicked(@NonNull MaxAd maxAd) {
                        AdLoader.getInstance().closeAds();
                    }

                    @Override
                    public void onAdLoadFailed(@NonNull String s, @NonNull MaxError maxError) {

                    }

                    @Override
                    public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {
                        onShowAdCompleteListener.onShowAdComplete();
                    }
                });
                AdLoader.log("APPOPEN -> AD SHOW");
                maxAppOpenAd.showAd();
            } else {
                onShowAdCompleteListener.onShowAdComplete();
            }

        } else {
            if (AdLoader.getFailedCountAppOpen() < MyApp.getAdModel().getAdsAppOpenFailedCount() && MyApp.getAdModel().getAdsAppOpen().equalsIgnoreCase(AdLoader.AD_GOOGLE) && MyApp.getAdModel().getAdsOnOff().equalsIgnoreCase("Yes")) {
                AdRequest request = getAdRequest();
                AdLoader.log("APPOPEN -> AD REQUEST");
                AppOpenAd.load(MyApp.getInstance(), MyApp.getAdModel().getAdsAppOpenId(), request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        AdLoader.increaseFailedCountAppOpen();
                        AdLoader.log("APPOPEN -> AD FAILED (" + AdLoader.getFailedCountAppOpen() + " of " + MyApp.getAdModel().getAdsAppOpenFailedCount() + ")\nKEY: " + MyApp.getAdModel().getAdsAppOpenId() + "ERROR: " + loadAdError.getMessage());
                        onShowAdCompleteListener.onShowAdComplete();
                    }

                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                        super.onAdLoaded(appOpenAd);
                        AdLoader.log("APPOPEN -> AD LOADED");
                        AdLoader.resetFailedCountAppOpen();
                        FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                appIsShowingAd = false;
                                fetchAd();
                                onShowAdCompleteListener.onShowAdComplete();
                            }

                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                                AdLoader.getInstance().closeAds();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                onShowAdCompleteListener.onShowAdComplete();
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                appIsShowingAd = true;
                            }
                        };
                        appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                        AdLoader.log("APPOPEN -> AD SHOW");
                        appOpenAd.show(appCurrentActivity);
                    }
                });
            } else if (PowerPreference.getDefaultFile().getBoolean(AdUtils.isAppLovinLoaded, false) && AdLoader.getFailedCountAppOpen() < MyApp.getAdModel().getAdsAppOpenFailedCount() && MyApp.getAdModel().getAdsAppOpen().equalsIgnoreCase(AdLoader.AD_APPLOVIN) && MyApp.getAdModel().getAdsOnOff().equalsIgnoreCase("Yes")) {
                MaxAppOpenAd maxAppOpenAd = new MaxAppOpenAd(MyApp.getAdModel().getAdsAppOpenId(), appCurrentActivity);
                maxAppOpenAd.setListener(new MaxAdListener() {
                    @Override
                    public void onAdLoaded(@NonNull MaxAd maxAd) {
                        AdLoader.log("APPOPEN -> AD LOADED");
                        AdLoader.resetFailedCountAppOpen();
                        maxAppOpenAd.showAd();
                    }

                    @Override
                    public void onAdDisplayed(@NonNull MaxAd maxAd) {
                        appIsShowingAd = true;
                    }

                    @Override
                    public void onAdHidden(@NonNull MaxAd maxAd) {
                        appIsShowingAd = false;
                        fetchAd();
                        onShowAdCompleteListener.onShowAdComplete();
                    }

                    @Override
                    public void onAdClicked(@NonNull MaxAd maxAd) {
                        AdLoader.getInstance().closeAds();
                    }

                    @Override
                    public void onAdLoadFailed(@NonNull String s, @NonNull MaxError maxError) {
                        AdLoader.increaseFailedCountAppOpen();
                        AdLoader.log("APPOPEN -> AD FAILED (" + AdLoader.getFailedCountAppOpen() + " of " + MyApp.getAdModel().getAdsAppOpenFailedCount() + ")\nKEY: " + MyApp.getAdModel().getAdsAppOpenId() + "ERROR: " + maxError.getMessage());
                        onShowAdCompleteListener.onShowAdComplete();
                    }

                    @Override
                    public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {
                        onShowAdCompleteListener.onShowAdComplete();
                    }
                });
                AdLoader.log("APPOPEN -> AD REQUEST");
                maxAppOpenAd.loadAd();
            } else {
                onShowAdCompleteListener.onShowAdComplete();
                AdLoader.log("APPOPEN -> FAILED COUNTER IS " + MyApp.getAdModel().getAdsAppOpenFailedCount());
            }
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
        appCurrentActivity = activity;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        appCurrentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        appCurrentActivity = activity;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        appCurrentActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        appCurrentActivity = activity;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        appCurrentActivity = activity;
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        appCurrentActivity = null;
    }

    @OnLifecycleEvent(ON_START)
    public void onStart() {
        if (!(appCurrentActivity.getClass().getName().equalsIgnoreCase(MyApp.className)) && MyApp.isShowAds && MyApp.getAdModel().getAdsOnOff().equalsIgnoreCase("Yes") && MyApp.getAdModel().getAppOpenBack().equalsIgnoreCase("Yes")) {
            showAdIfAvailable();
        }
    }

}