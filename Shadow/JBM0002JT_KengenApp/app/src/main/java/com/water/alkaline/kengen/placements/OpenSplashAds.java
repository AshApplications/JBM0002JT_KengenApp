package com.water.alkaline.kengen.placements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.ui.activity.SplashActivity;
import com.water.alkaline.kengen.utils.Constant;

import java.util.Objects;

public class OpenSplashAds {

    @SuppressLint("StaticFieldLeak")
    public static AppOpenAd appOpenAd1 = null;

    public static MyApplication Application = MyApplication.getInstance();
    @SuppressLint("StaticFieldLeak")
    public static Activity currentActivity;

    public static OnAdClosedListener mOnAdClosedListener;

    public static Dialog mDialog = null;
    public static boolean isLoading = true;

    public OpenSplashAds() {
    }

    private static AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    public void loadOpenAd(Activity activity) {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleAdsOnOff, false) && (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleSplashOpenAdsOnOff, false) || PowerPreference.getDefaultFile().getBoolean(Constant.GoogleExitSplashInterOnOff, false))) {
            currentActivity = activity;
            isLoading = true;

            Log.e("TAG", "splash load loadOpenAd");

            AppOpenAd.AppOpenAdLoadCallback loadCallback1 = new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdLoaded(AppOpenAd ad) {
                    appOpenAd1 = ad;
                    isLoading = false;
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    appOpenAd1 = null;
                    isLoading = false;
                }
            };

            final String appOpenAd = PowerPreference.getDefaultFile().getString(Constant.OPENAD, "123");
            AdRequest request = getAdRequest();
            AppOpenAd.load(Application, appOpenAd, request,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback1);
        }
    }

    public void showOpenAd(Activity activity, OnAdClosedListener onAdClosedListener) {
        currentActivity = activity;
        mOnAdClosedListener = onAdClosedListener;
        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, true)) {
            if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleAdsOnOff, false) && (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleSplashOpenAdsOnOff, false) || PowerPreference.getDefaultFile().getBoolean(Constant.GoogleExitSplashInterOnOff, false))) {
                if (isLoading) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOpenAd(activity, onAdClosedListener);
                        }
                    }, 2000);
                } else {
                    if (appOpenAd1 != null)
                        showAd(activity, onAdClosedListener);
                    else
                        showQurekaDialog(currentActivity, onAdClosedListener);
                }
            } else {
                showQurekaDialog(currentActivity, onAdClosedListener);
            }
        } else {
            if (onAdClosedListener != null)
                onAdClosedListener.onAdClosed();
        }
    }

    public void showAd(Activity activity, OnAdClosedListener onAdClosedListener) {
        FullScreenContentCallback fullScreenContentCallback =
                new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        appOpenAd1 = null;

                        if (activity instanceof SplashActivity && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleExitSplashInterOnOff, false)) {
                            loadOpenAd(activity);
                        }

                        if (onAdClosedListener != null)
                            onAdClosedListener.onAdClosed();

                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        MainAds.closeGoogleAds();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        appOpenAd1 = null;
                        showQurekaDialog(currentActivity, null);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {

                    }
                };

        appOpenAd1.setFullScreenContentCallback(fullScreenContentCallback);
        appOpenAd1.show(currentActivity);
    }

    public void showQurekaDialog(Activity activity, OnAdClosedListener onAdClosedListener) {

        if (activity instanceof SplashActivity && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleExitSplashInterOnOff, false)) {
            loadOpenAd(activity);
        }

        if (PowerPreference.getDefaultFile().getBoolean(Constant.QurekaOnOff, true) && (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleSplashOpenAdsOnOff, false) || PowerPreference.getDefaultFile().getBoolean(Constant.GoogleExitSplashInterOnOff, false))) {

            try {

                mDialog = new Dialog(activity);
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDialog.setContentView(R.layout.qureka_open);
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mDialog.setCancelable(false);
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                ImageView imageView = mDialog.findViewById(R.id.qurekaAds);
                ImageView imageView1 = mDialog.findViewById(R.id.gif_inter_round);
                Constant.setQureka(activity, imageView, null, imageView1, Constant.QOPEN_COUNT);

                LinearLayout qurekaAdLayout = mDialog.findViewById(R.id.qurekaAdLayout);
                LinearLayout qurekaAdsClose = mDialog.findViewById(R.id.qurekaAdsClose);
                qurekaAdLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Constant.gotoAds(activity);
                    }
                });

                qurekaAdsClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                    }
                });

                mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        if (OpenSplashAds.mOnAdClosedListener != null)
                            OpenSplashAds.mOnAdClosedListener.onAdClosed();
                    }
                });

                mDialog.show();

            } catch (Exception e) {
                Log.w("Catch", Objects.requireNonNull(e.getMessage()));
            }
        } else {
            if (onAdClosedListener != null)
                onAdClosedListener.onAdClosed();
        }
    }

    public interface OnAdClosedListener {
        public void onAdClosed();
    }
}
