package com.google.gms.ads;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.preference.PowerPreference;

import java.util.Objects;

public class InterAds {

    public static Activity mActivity;

    public static InterstitialAd mInterstitialAd;
    public static OnAdClosedListener mOnAdClosedListener;

    public Dialog mLoadingDialog;

    protected void ShowProgress(Activity activity) {
        if (!activity.getClass().getName().equalsIgnoreCase(PowerPreference.getDefaultFile().getString(AdUtils.activitySplash))) {
            if (mLoadingDialog == null && !activity.isFinishing()) {
                mLoadingDialog = showScreenDataLoader(activity);
            }
            if (!activity.isFinishing() && mLoadingDialog != null && !mLoadingDialog.isShowing())
                mLoadingDialog.show();
        }
    }

    public static Dialog showScreenDataLoader(Activity mActivity) {
        Dialog d = new Dialog(mActivity, R.style.NormalDialog);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(d.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        d.setContentView(R.layout.dialog_ad_loader);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        d.setCancelable(false);
        d.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = -2;
        lp.height = -2;
        d.show();
        d.getWindow().setAttributes(lp);
        return d;
    }

    protected void HideProgress(Activity activity) {
        if (!activity.isFinishing() && mLoadingDialog != null && mLoadingDialog.isShowing())
            mLoadingDialog.dismiss();
    }


    public interface OnAdClosedListener {
        public void onAdClosed();
    }

    public void loadInterAds(Activity context) {
        final String interAd = PowerPreference.getDefaultFile().getString(AdUtils.INTERID, "123");

        if (mInterstitialAd == null && !interAd.equalsIgnoreCase("") && PowerPreference.getDefaultFile().getInt(AdUtils.SERVER_INTERVAL_COUNT, 0) > 0 && PowerPreference.getDefaultFile().getBoolean(AdUtils.GoogleAdsOnOff)) {
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(context, interAd, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    mInterstitialAd = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    AdUtils.showLog(loadAdError.toString());
                    mInterstitialAd = null;
                }
            });
        }
    }

    public void showInterAds(Activity context, OnAdClosedListener onAdClosedListener) {
        mActivity = context;
        mOnAdClosedListener = onAdClosedListener;

        int custGCount = PowerPreference.getDefaultFile().getInt(AdUtils.SERVER_INTERVAL_COUNT);
        int appGCount = PowerPreference.getDefaultFile().getInt(AdUtils.APP_INTERVAL_COUNT);

        if (custGCount != 0 && appGCount % custGCount == 0) {
            appGCount++;
            PowerPreference.getDefaultFile().putInt(AdUtils.APP_INTERVAL_COUNT, appGCount);
            watchAds(context, onAdClosedListener);
        } else {
            appGCount++;
            PowerPreference.getDefaultFile().putInt(AdUtils.APP_INTERVAL_COUNT, appGCount);
            if (mOnAdClosedListener != null)
                mOnAdClosedListener.onAdClosed();
        }
    }

    public void showSplashAds(Activity context, OnAdClosedListener onAdClosedListener) {
        int appGCount = PowerPreference.getDefaultFile().getInt(AdUtils.APP_INTERVAL_COUNT);
        appGCount++;
        PowerPreference.getDefaultFile().putInt(AdUtils.APP_INTERVAL_COUNT, appGCount);
        watchAds(context, onAdClosedListener);
    }

    public void watchAds(Activity context, OnAdClosedListener onAdClosedListener) {
        mActivity = context;
        mOnAdClosedListener = onAdClosedListener;
        if (!PowerPreference.getDefaultFile().getBoolean(AdUtils.GoogleAdsOnOff, false)) {
            showQureka(context);
        } else if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {

                    if (mOnAdClosedListener != null) {
                        mOnAdClosedListener.onAdClosed();
                    }

                    mInterstitialAd = null;
                    loadInterAds(context);
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    AdUtils.closeGoogleAds();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    AdUtils.showLog(adError.toString());

                    if (mOnAdClosedListener != null)
                        mOnAdClosedListener.onAdClosed();

                    mInterstitialAd = null;
                    loadInterAds(context);

                }

                @Override
                public void onAdShowedFullScreenContent() {
                }
            });

            if (PowerPreference.getDefaultFile().getBoolean(AdUtils.ShowDialogBeforeAds, true)) {
                ShowProgress(context);
                new Handler().postDelayed(() -> {
                    HideProgress(context);
                    mInterstitialAd.show(context);
                }, (long) (PowerPreference.getDefaultFile().getDouble(AdUtils.DialogTimeInSec, 1) * 1000L));

            } else {
                mInterstitialAd.show(context);
            }

        } else {
            showInterAds1(context);
        }
    }


    public void showInterAds1(Activity context) {

        final String interAd = PowerPreference.getDefaultFile().getString(AdUtils.INTERID, "123");
        if (!interAd.equalsIgnoreCase("")) {
            AdRequest adRequest = new AdRequest.Builder().build();
            ShowProgress(context);
            InterstitialAd.load(context, interAd, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    HideProgress(context);
                    interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            showQureka(context);
                            loadInterAds(context);
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            if (mOnAdClosedListener != null)
                                mOnAdClosedListener.onAdClosed();
                            loadInterAds(context);
                        }

                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                            AdUtils.closeGoogleAds();
                        }
                    });
                    interstitialAd.show(context);
                }


                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    AdUtils.showLog(loadAdError.toString());
                    HideProgress(context);
                    showQureka(context);
                    loadInterAds(context);
                }
            });
        } else {
            showQureka(context);
            loadInterAds(context);
        }
    }

    public void showQureka(Activity context) {
        if (PowerPreference.getDefaultFile().getBoolean(AdUtils.QurekaOnOff, false)) {
            Intent intent = new Intent(context, QurekaActivity.class);
            intent.putExtra(AdUtils.BACK_ADS, false);
            context.startActivity(intent);
        } else {
            if (mOnAdClosedListener != null)
                mOnAdClosedListener.onAdClosed();
        }
    }
}
