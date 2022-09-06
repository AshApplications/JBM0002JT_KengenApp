package com.water.alkaline.kengen.placements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.databinding.ActivityQurekaInterBinding;
import com.water.alkaline.kengen.ui.activity.QurekaInterActivity;
import com.water.alkaline.kengen.ui.activity.SplashActivity;
import com.water.alkaline.kengen.utils.Constant;

import java.util.Objects;

public class InterSplashAds {

    @SuppressLint("StaticFieldLeak")
    public static Activity mActivity;

    public static boolean isLoading = false;

    public static InterstitialAd mInterstitialAd;
    public static OnAdClosedListener mOnAdClosedListener;

    public Dialog mLoadingDialog;

    public static Dialog mDialog = null;

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

    public void loadInterAds(Activity activity) {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleAdsOnOff, false) && (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleSplashOpenAdsOnOff, false) || PowerPreference.getDefaultFile().getBoolean(Constant.GoogleExitSplashInterOnOff, false))) {
            final String interAd = PowerPreference.getDefaultFile().getString(Constant.INTERID, "123");
            AdRequest adRequest = new AdRequest.Builder().build();
            isLoading = true;

            Log.e("TAG", "splash load interAds");
            InterstitialAd.load(activity, interAd, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    mInterstitialAd = interstitialAd;
                    isLoading = false;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mInterstitialAd = null;
                    isLoading = false;

                }
            });
        }
    }

    public void showInterAds(Activity context, OnAdClosedListener onAdClosedListener) {
        mActivity = context;
        mOnAdClosedListener = onAdClosedListener;
        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, true)) {
            if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleAdsOnOff, false) && (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleSplashOpenAdsOnOff, false) || PowerPreference.getDefaultFile().getBoolean(Constant.GoogleExitSplashInterOnOff, false))) {
                if (isLoading) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showInterAds(context, onAdClosedListener);
                        }
                    }, 2000);
                } else {
                    if (mInterstitialAd != null)
                        showAd(context, onAdClosedListener);
                    else
                        showQurekaAds(context, onAdClosedListener);
                }
            } else {
                showQurekaAds(context, onAdClosedListener);
            }
        } else {
            if (onAdClosedListener != null)
                onAdClosedListener.onAdClosed();
        }
    }

    public void showAd(Activity context, OnAdClosedListener onAdClosedListener) {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.ShowDialogBeforeAds, true)) {
            ShowProgress(context);
            new Handler().postDelayed(() -> {
                HideProgress(context);
                setmOnAdClosedListener(context, onAdClosedListener);
                mInterstitialAd.show(context);
            }, (long) (PowerPreference.getDefaultFile().getDouble(Constant.DialogTimeInSec, 1) * 1000L));
        } else {
            setmOnAdClosedListener(context, onAdClosedListener);
            mInterstitialAd.show(context);
        }
    }

    public void setmOnAdClosedListener(Activity activity, OnAdClosedListener onAdClosedListener) {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {

                if (mActivity instanceof SplashActivity && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleExitSplashInterOnOff, false)) {
                    loadInterAds(activity);
                }

                if (mOnAdClosedListener != null) {
                    mOnAdClosedListener.onAdClosed();
                }
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                MainAds.closeGoogleAds();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                mInterstitialAd = null;

                if (mActivity instanceof SplashActivity && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleExitSplashInterOnOff, false)) {
                    loadInterAds(activity);
                }

                if (PowerPreference.getDefaultFile().getBoolean(Constant.QurekaOnOff, true)) {
                    showQurekaAds(activity, mOnAdClosedListener);
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

    public void showQurekaAds(Activity activity, OnAdClosedListener onAdClosedListener) {

        if (activity instanceof SplashActivity && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleExitSplashInterOnOff, false)) {
            loadInterAds(activity);
        }

        if (PowerPreference.getDefaultFile().getBoolean(Constant.QurekaOnOff, true) && (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleSplashOpenAdsOnOff, false) || PowerPreference.getDefaultFile().getBoolean(Constant.GoogleExitSplashInterOnOff, false))) {
            Intent intent = new Intent(activity, QurekaInterActivity.class);
            intent.putExtra(Constant.BACK_ADS, false);
            intent.putExtra(Constant.SPLASH_ADS, true);
            activity.startActivity(intent);
        } else {
            if (mOnAdClosedListener != null)
                mOnAdClosedListener.onAdClosed();
        }
    }

}
