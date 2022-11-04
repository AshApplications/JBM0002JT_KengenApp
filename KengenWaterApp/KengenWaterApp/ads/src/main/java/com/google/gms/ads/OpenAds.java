package com.google.gms.ads;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.gms.ads.databinding.QurekaOpenBinding;
import com.preference.PowerPreference;

import java.util.Objects;

public class OpenAds implements LifecycleObserver, android.app.Application.ActivityLifecycleCallbacks {

    @SuppressLint("StaticFieldLeak")
    public static OpenAds mAppAds;

    public static AppOpenAd appOpenAd1 = null;

    public static Dialog mDialog = null;
    public static final CustomApplication Application = CustomApplication.getInstance();
    public static Activity currentActivity;

    private boolean isOpenAdLoaded = false;

    public static boolean isShowingAd = false;

    public interface OnAdClosedListener {
        public void onAdClosed();
    }

    public OpenAds() {
        if (mAppAds == null) {
            mAppAds = this;
            Application.registerActivityLifecycleCallbacks(this);
            ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        }
    }

    public void loadOpenAd() {
        final String appOpenAd = PowerPreference.getDefaultFile().getString(AdUtils.OPENAD, "123");

        if (!isOpenAdLoaded && !appOpenAd.equalsIgnoreCase("") && PowerPreference.getDefaultFile().getBoolean(AdUtils.GoogleAdsOnOff)) {
            AppOpenAd.AppOpenAdLoadCallback loadCallback1 = new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdLoaded(AppOpenAd ad) {
                    appOpenAd1 = ad;
                    isOpenAdLoaded = true;
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    AdUtils.showLog(loadAdError.toString());
                    appOpenAd1 = null;
                    isOpenAdLoaded = false;
                }
            };

            AdRequest request = getAdRequest();
            AppOpenAd.load(Application, appOpenAd, request,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback1);
        }
    }


    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    public void showOpenAd(Activity activity, OnAdClosedListener onAdClosedListener) {

        if (!PowerPreference.getDefaultFile().getBoolean(AdUtils.GoogleAdsOnOff, false)) {
            showQurekaDialog(activity, onAdClosedListener);
        } else if (onAdClosedListener != null) {
            showOpenAds1(activity, onAdClosedListener);
        } else if (appOpenAd1 != null && !isShowingAd) {
            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            appOpenAd1 = null;
                            isShowingAd = false;

                            isOpenAdLoaded = false;
                            loadOpenAd();
                        }

                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                            AdUtils.closeGoogleAds();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            AdUtils.showLog(adError.toString());
                            appOpenAd1 = null;
                            isShowingAd = false;

                            isOpenAdLoaded = false;
                            showQurekaDialog(activity, onAdClosedListener);
                            loadOpenAd();

                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                            if (PowerPreference.getDefaultFile().getInt(AdUtils.AppOpenTime, 0) == 1) {
                                PowerPreference.getDefaultFile().putBoolean(AdUtils.APP_OPEN_SHOW, true);
                            }
                        }
                    };

            appOpenAd1.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd1.show(activity);

        } else {
            showQurekaDialog(activity, onAdClosedListener);
        }
    }

    public void showOpenAds1(Activity activity, OnAdClosedListener onAdClosedListener) {
        final String appOpenAd = PowerPreference.getDefaultFile().getString(AdUtils.OPENAD, "123");

        if (!appOpenAd.equalsIgnoreCase("")) {
            AppOpenAd.AppOpenAdLoadCallback loadCallback1 = new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdLoaded(AppOpenAd ad) {
                    FullScreenContentCallback fullScreenContentCallback =
                            new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    onAdClosedListener.onAdClosed();
                                }

                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                    AdUtils.closeGoogleAds();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    showQurekaDialog(activity, onAdClosedListener);
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    if (PowerPreference.getDefaultFile().getInt(AdUtils.AppOpenTime, 0) == 1) {
                                        PowerPreference.getDefaultFile().putBoolean(AdUtils.APP_OPEN_SHOW, true);
                                    }
                                }
                            };

                    ad.setFullScreenContentCallback(fullScreenContentCallback);
                    ad.show(activity);
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    AdUtils.showLog(loadAdError.toString());
                    showQurekaDialog(activity, onAdClosedListener);
                }
            };

            AdRequest request = getAdRequest();
            AppOpenAd.load(Application, appOpenAd, request,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback1);
        } else {
            showQurekaDialog(activity, onAdClosedListener);
        }
    }

    public void showQurekaDialog(Activity activity, OnAdClosedListener onAdClosedListener) {

        if (PowerPreference.getDefaultFile().getBoolean(AdUtils.QurekaOnOff, true)) {

            try {
                mDialog = new Dialog(activity);
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                QurekaOpenBinding binding = QurekaOpenBinding.inflate(activity.getLayoutInflater());
                mDialog.setContentView(binding.getRoot());
                mDialog.setCancelable(false);
                Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                Glide.with(activity).asGif().load(R.drawable.qureka_round1).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.adMedia);
                binding.qurekaAdLayout.setOnClickListener(view -> AdUtils.gotoAds(activity));
                binding.qurekaAdsClose.setOnClickListener(view -> mDialog.dismiss());
                mDialog.setOnShowListener(dialog -> isShowingAd = true);

                mDialog.setOnDismissListener(dialog -> {
                    if (onAdClosedListener != null)
                        onAdClosedListener.onAdClosed();
                });
                mDialog.show();

            } catch (Exception e) {
                AdUtils.showLog(e.toString());
                if (onAdClosedListener != null)
                    onAdClosedListener.onAdClosed();
            }
        } else {
            if (onAdClosedListener != null)
                onAdClosedListener.onAdClosed();
        }
    }

    @OnLifecycleEvent(ON_START)
    public void onStart() {
        if (PowerPreference.getDefaultFile().getBoolean(AdUtils.AdsOnOff, true)) {
            if (currentActivity != null && !currentActivity.getClass().getName().equalsIgnoreCase(PowerPreference.getDefaultFile().getString(AdUtils.activitySplash)))
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                } else {
                    new MainAds().showOpenAds(currentActivity, null);
                }
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
