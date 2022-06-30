package com.water.alkaline.kengen.placements;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.ui.activity.QurekaInterActivity;
import com.water.alkaline.kengen.utils.Constant;

import java.util.Objects;

public class BackInterAds {

    public static Activity mActivity;

    public static InterstitialAd mInterstitialAd;
    public static OnAdClosedListener mOnAdClosedListener;

    public Dialog mLoadingDialog;

    protected void ShowProgress(Activity activity) {
        if (mLoadingDialog == null && !activity.isFinishing()) {
            mLoadingDialog = showScreenDataLoader(activity);
        }
        if (!activity.isFinishing() && mLoadingDialog != null && !mLoadingDialog.isShowing())
            mLoadingDialog.show();
    }

    public static Dialog showScreenDataLoader(Activity mActivity) {
        Dialog d = new Dialog(mActivity, R.style.NormalDialog);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(d.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        d.setContentView(R.layout.dialog_inter_loading);
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
        if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleAdsOnOff,false ) && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleBackInterOnOff, false)) {
            final String interAd = PowerPreference.getDefaultFile().getString(Constant.INTERID, "123");
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(context, interAd, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    mInterstitialAd = interstitialAd;

                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {

                            loadInterAds(context);

                            if (mOnAdClosedListener != null) {
                                mOnAdClosedListener.onAdClosed();
                            }
                        }

                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                            int clickCOunt = PowerPreference.getDefaultFile().getInt(Constant.APP_CLICK_COUNT, 0);
                            PowerPreference.getDefaultFile().putInt(Constant.APP_CLICK_COUNT, clickCOunt + 1);
                            int clickCOunt2 = PowerPreference.getDefaultFile().getInt(Constant.APP_CLICK_COUNT, 0);

                            if (clickCOunt2 >= PowerPreference.getDefaultFile().getInt(Constant.AD_CLICK_COUNT, 3)) {
                                PowerPreference.getDefaultFile().putBoolean(Constant.GoogleAdsOnOff,false );
                            }
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                            mInterstitialAd = null;

                            Constant.showLog("The ad failed to show.");
                            loadInterAds(mActivity);

                            if (PowerPreference.getDefaultFile().getBoolean(Constant.QurekaOnOff, false) && PowerPreference.getDefaultFile().getBoolean(Constant.QurekaBackInterOnOff, false)) {
                                showQurekaAds(mActivity, mOnAdClosedListener);
                            } else {
                                if (mOnAdClosedListener != null)
                                    mOnAdClosedListener.onAdClosed();
                            }

                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd = null;
                        }
                    });
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mInterstitialAd = null;
                }
            });
        }
    }


    public void showInterAds(Activity context, OnAdClosedListener onAdClosedListener) {
        mActivity = context;
        mOnAdClosedListener = onAdClosedListener;

        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, true)) {
            int custGCount = PowerPreference.getDefaultFile().getInt(Constant.SERVER_BACK_COUNT);
            int appGCount = PowerPreference.getDefaultFile().getInt(Constant.APP_BACK_COUNT);

            if (custGCount != 0 && appGCount % custGCount == 0) {
                checkInterAds(context, onAdClosedListener);
            } else {
                appGCount++;
                PowerPreference.getDefaultFile().putInt(Constant.APP_BACK_COUNT, appGCount);
                if (mOnAdClosedListener != null)
                    mOnAdClosedListener.onAdClosed();
            }
        } else {
            if (mOnAdClosedListener != null)
                mOnAdClosedListener.onAdClosed();
        }
    }


    public void checkInterAds(Activity context, OnAdClosedListener onAdClosedListener) {
        mActivity = context;
        mOnAdClosedListener = onAdClosedListener;
        if (mInterstitialAd != null && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleAdsOnOff,false ) && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleBackInterOnOff, false)) {
            if (PowerPreference.getDefaultFile().getBoolean(Constant.ShowDialogBeforeAds, true)) {
                ShowProgress(context);
                new Handler().postDelayed(() -> {

                    HideProgress(context);
                    int appGCount = PowerPreference.getDefaultFile().getInt(Constant.APP_BACK_COUNT);

                    appGCount++;
                    PowerPreference.getDefaultFile().putInt(Constant.APP_BACK_COUNT, appGCount);
                    mInterstitialAd.show(context);

                }, (long) (PowerPreference.getDefaultFile().getDouble(Constant.DialogTimeInSec, 1) * 1000L));

            } else {
                int appGCount = PowerPreference.getDefaultFile().getInt(Constant.APP_BACK_COUNT);

                appGCount++;
                PowerPreference.getDefaultFile().putInt(Constant.APP_BACK_COUNT, appGCount);
                mInterstitialAd.show(context);
            }

        } else {
            loadInterAds(context);

            if (PowerPreference.getDefaultFile().getBoolean(Constant.QurekaOnOff, false) && PowerPreference.getDefaultFile().getBoolean(Constant.QurekaBackInterOnOff, false)) {

                if (PowerPreference.getDefaultFile().getBoolean(Constant.ShowDialogBeforeAds, true)) {
                    ShowProgress(context);
                    new Handler().postDelayed(() -> {

                        HideProgress(context);
                        int appGCount = PowerPreference.getDefaultFile().getInt(Constant.APP_BACK_COUNT);

                        appGCount++;
                        PowerPreference.getDefaultFile().putInt(Constant.APP_BACK_COUNT, appGCount);

                        showQurekaAds(context, onAdClosedListener);
                    }, (long) (PowerPreference.getDefaultFile().getDouble(Constant.DialogTimeInSec, 1) * 1000L));

                } else {
                    int appGCount = PowerPreference.getDefaultFile().getInt(Constant.APP_BACK_COUNT);

                    appGCount++;
                    PowerPreference.getDefaultFile().putInt(Constant.APP_BACK_COUNT, appGCount);

                    showQurekaAds(context, onAdClosedListener);
                }

            } else {
                if (mOnAdClosedListener != null)
                    mOnAdClosedListener.onAdClosed();
            }
        }
    }

    public void showQurekaAds(Activity activity, OnAdClosedListener onAdClosedListener) {
        Intent intent = new Intent(activity, QurekaInterActivity.class);
        intent.putExtra(Constant.BACK_ADS, true);
        activity.startActivity(intent);
    }
}
