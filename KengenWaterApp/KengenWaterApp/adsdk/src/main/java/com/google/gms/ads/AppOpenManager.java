package com.google.gms.ads;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;

public class AppOpenManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {

    public static boolean appIsShowingAd = false;
    public AppOpenAd appOpenAd = null;
    public AppOpenAd.AppOpenAdLoadCallback appCallback;
    public Activity appCurrentActivity;
    public long appLoadTime = 0;

    public AppOpenManager() {
        MyApp.getInstance().registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    public void fetchAd() {

        if (isAdAvailable()) {
            return;
        }
        if (AdLoader.getFailedCountAppOpen() < MyApp.getAdModel().getAdsAppOpenFailedCount() && MyApp.getAdModel().getAdsAppOpen().equalsIgnoreCase(AdLoader.AD_GOOGLE) && MyApp.getAdModel().getAdsOnOff().equalsIgnoreCase("Yes")) {
            appCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull AppOpenAd ad) {
                    AdLoader.log("APPOPEN -> AD LOADED");
                    AppOpenManager.this.appOpenAd = ad;
                    AppOpenManager.this.appLoadTime = (new Date()).getTime();
                    AdLoader.resetFailedCountAppOpen();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    AdLoader.increaseFailedCountAppOpen();
                    AdLoader.log("APPOPEN -> AD FAILED (" + AdLoader.getFailedCountAppOpen() + " of " + MyApp.getAdModel().getAdsAppOpenFailedCount() + ")\nKEY: " + MyApp.getAdModel().getAdsAppOpenId() + "ERROR: " + loadAdError.getMessage());
                }
            };
            AdRequest request = getAdRequest();
            AdLoader.log("APPOPEN -> AD REQUEST");
            AppOpenAd.load(MyApp.getInstance(), MyApp.getAdModel().getAdsAppOpenId(), request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, appCallback);
        } else {
            AdLoader.log("APPOPEN -> FAILED COUNTER IS " + MyApp.getAdModel().getAdsAppOpenFailedCount());
        }
    }


    public AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    public void showAdIfAvailable() {
        if (!AdLoader.getInstance().isInterstitialShowing && !appIsShowingAd && isAdAvailable() && MyApp.getAdModel().getAdsOnOff().equalsIgnoreCase("Yes")) {
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    AppOpenManager.this.appOpenAd = null;
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
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            appOpenAd.show(appCurrentActivity);
        } else {
            fetchAd();
        }
    }

    public void showAdIfSplashAvailable(@NonNull final Activity activity, @NonNull MyApp.OnShowAdCompleteListener onShowAdCompleteListener) {
        if (!appIsShowingAd && isAdAvailable() && MyApp.getAdModel().getAdsOnOff().equalsIgnoreCase("Yes")) {
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    AppOpenManager.this.appOpenAd = null;
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
            appOpenAd.show(activity);
        } else {
            if (AdLoader.getFailedCountAppOpen() < MyApp.getAdModel().getAdsAppOpenFailedCount() && MyApp.getAdModel().getAdsAppOpen().equalsIgnoreCase(AdLoader.AD_GOOGLE) && MyApp.getAdModel().getAdsOnOff().equalsIgnoreCase("Yes")) {
                appCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        AdLoader.log("APPOPEN -> AD LOADED");
                        AdLoader.resetFailedCountAppOpen();
                        AppOpenManager.this.appOpenAd = ad;
                        AppOpenManager.this.appLoadTime = (new Date()).getTime();
                        FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                AppOpenManager.this.appOpenAd = null;
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


                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        AdLoader.increaseFailedCountAppOpen();
                        AdLoader.log("APPOPEN -> AD FAILED (" + AdLoader.getFailedCountAppOpen() + " of " + MyApp.getAdModel().getAdsAppOpenFailedCount() + ")\nKEY: " + MyApp.getAdModel().getAdsAppOpenId() + "ERROR: " + loadAdError.getMessage());
                        onShowAdCompleteListener.onShowAdComplete();
                    }
                };
                AdRequest request = getAdRequest();
                AdLoader.log("APPOPEN -> AD REQUEST");
                AppOpenAd.load(MyApp.getInstance(), MyApp.getAdModel().getAdsAppOpenId(), request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, appCallback);
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

    public boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.appLoadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }
}