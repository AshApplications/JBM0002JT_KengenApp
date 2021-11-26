package com.water.alkaline.kengen.placements;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.utils.Constant;

import java.util.Date;

public class OpenAds implements LifecycleObserver, android.app.Application.ActivityLifecycleCallbacks {

    private AppOpenAd appOpenAdGoogle = null;
    private AppOpenAd appOpenAdFb = null;

    private final MyApplication Application;
    private Activity currentActivity;

    private long loadTime1 = 0;

    private static boolean isShowingAd = false;

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        PowerPreference.getDefaultFile().putInt("isBackground", 1);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        PowerPreference.getDefaultFile().putInt("isBackground", 1);
    }

    public OpenAds(MyApplication Application) {
        this.Application = Application;
        this.Application.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    private void loadGOpen() {
        AppOpenAd.AppOpenAdLoadCallback loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(AppOpenAd ad) {

                appOpenAdGoogle = ad;
                OpenAds.this.loadTime1 = (new Date()).getTime();
                PowerPreference.getDefaultFile().putInt(Constant.APP_POSITION, -1);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                 appOpenAdGoogle = null;
            }
        };

        final String appOpenAd = PowerPreference.getDefaultFile().getString(Constant.G_APPID);
        AdRequest request = getAdRequest();
        AppOpenAd.load(Application, appOpenAd, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);

    }

    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }


    public void showGOpenAd() {
        if (appOpenAdGoogle != null) {
            if (!isShowingAd) {
                FullScreenContentCallback fullScreenContentCallback =
                        new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                appOpenAdGoogle = null;
                                isShowingAd = false;

                                PowerPreference.getDefaultFile().putInt(Constant.APP_POSITION, -1);
                                loadGOpen();
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
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                appOpenAdGoogle = null;
                                isShowingAd = false;

                                loadGOpen();
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                isShowingAd = true;
                            }
                        };

                appOpenAdGoogle.setFullScreenContentCallback(fullScreenContentCallback);
                appOpenAdGoogle.show(currentActivity);
            }
        } else {
            loadGOpen();
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (PowerPreference.getDefaultFile().getInt(Constant.QUREKA, 5) <= 0) {
            showGOpenAd();
        }
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        currentActivity = null;
    }
}
