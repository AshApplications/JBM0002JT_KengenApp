package com.water.alkaline.kengen.placements;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.InterstitialCallbacks;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.utils.Constant;

import java.util.Objects;

public class InterAds {

    public static boolean apCheck = false;

    private static boolean isGinterloaded = false;
    private static boolean isApinterloaded = false;

    public static InterstitialAd gInterstitialAd;
    public static OnAdClosedListener mOnAdClosedListener;

    public Dialog mLoadingDialog;

    public interface OnAdClosedListener {
        public void onAdClosed();
    }

    protected void ShowProgress(Activity activity) {
        if (mLoadingDialog == null && !activity.isFinishing())
            mLoadingDialog = showScreenDataLoader(activity);

        if (!activity.isFinishing() && mLoadingDialog != null && !mLoadingDialog.isShowing())
            mLoadingDialog.show();
    }

    public static Dialog showScreenDataLoader(Activity mActivity) {
        Dialog d = new Dialog(mActivity);
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

    public void loadInter(Activity activity) {
        PowerPreference.getDefaultFile().putInt(Constant.INTER_POSITION, -1);
        loadMainInter(activity);
    }

    public void loadMainInter(Activity activity) {
        int position = PowerPreference.getDefaultFile().getInt(Constant.INTER_POSITION);
        PowerPreference.getDefaultFile().putInt(Constant.INTER_POSITION, position + 1);
        int position2 = PowerPreference.getDefaultFile().getInt(Constant.INTER_POSITION);

        if (position2 < MyApplication.arrayList.size()) {
            Log.e("TAG", "loadMainInter " + MyApplication.arrayList.get(position2));
            switch (MyApplication.arrayList.get(position2)) {
                case 0:
                    loadGInter(activity);
                    break;
                case 1:
                    loadApInter(activity);
                    break;
                default:
                    loadMainInter(activity);
                    break;
            }
        } else {
            PowerPreference.getDefaultFile().putInt(Constant.INTER_POSITION, -1);
        }
    }


    private void loadGInter(Activity activity) {
        Log.e("TAG", "loadGInter ");
        final String interAd = PowerPreference.getDefaultFile().getString(Constant.G_INTERID);
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activity, interAd, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                Log.e("TAG", "loadGInter onAdLoaded");
                isGinterloaded = true;
                gInterstitialAd = interstitialAd;
                PowerPreference.getDefaultFile().putInt(Constant.INTER_POSITION, -1);

                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);

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
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();

                        loadInter(activity);

                        BannerAds.adCheck = true;

                        if (mOnAdClosedListener != null) {
                            mOnAdClosedListener.onAdClosed();
                        }

                    }
                });
            }


            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e("TAG", "loadGInter onAdFailedToLoad");
                isGinterloaded = false;
                loadMainInter(activity);
            }
        });
    }
    private void loadApInter(Activity activity) {
        Log.e("TAG", "loadApInter ");
        apCheck = true;
        Appodeal.cache(activity,Appodeal.INTERSTITIAL);
        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean isPrecache) {
                Log.e("TAG", "loadApInter onInterstitialLoaded");
                isApinterloaded = true;
                PowerPreference.getDefaultFile().putInt(Constant.INTER_POSITION, -1);
            }

            @Override
            public void onInterstitialFailedToLoad() {
                Log.e("TAG", "loadApInter onAdFailedToLoad");
                if (apCheck) {
                    apCheck = false;
                    isApinterloaded = false;
                    loadMainInter(activity);
                }
            }

            @Override
            public void onInterstitialShown() {

            }

            @Override
            public void onInterstitialShowFailed() {

            }

            @Override
            public void onInterstitialClicked() {
                int clickCOunt = PowerPreference.getDefaultFile().getInt(Constant.APP_CLICK_COUNT, 0);
                PowerPreference.getDefaultFile().putInt(Constant.APP_CLICK_COUNT, clickCOunt + 1);
                int clickCOunt2 = PowerPreference.getDefaultFile().getInt(Constant.APP_CLICK_COUNT, 0);

                if (clickCOunt2 >= PowerPreference.getDefaultFile().getInt(Constant.AD_CLICK_COUNT, 3)) {
                    PowerPreference.getDefaultFile().putInt(Constant.QUREKA, 99999);
                }
            }

            @Override
            public void onInterstitialClosed() {
                loadInter(activity);

                BannerAds.adCheck = true;

                if (mOnAdClosedListener != null) {
                    mOnAdClosedListener.onAdClosed();
                }
            }

            @Override
            public void onInterstitialExpired() {

            }
        });

    }

    public void showInter(Activity activity, OnAdClosedListener listener) {

        if (PowerPreference.getDefaultFile().getInt(Constant.QUREKA, 5) > 0) {

            if (listener != null)
                listener.onAdClosed();

            return;
        }


        int custGCount = PowerPreference.getDefaultFile().getInt(Constant.SERVER_INTERVAL_COUNT);
        int appGCount = PowerPreference.getDefaultFile().getInt(Constant.APP_INTERVAL_COUNT);


        if (appGCount % custGCount == 0) {

            PowerPreference.getDefaultFile().putInt(Constant.INTER_POSITION, -1);
            showMainInter(activity, listener);
        } else {

            appGCount++;
            PowerPreference.getDefaultFile().putInt(Constant.APP_INTERVAL_COUNT, appGCount);

            if (listener != null)
                listener.onAdClosed();
        }
    }


    public void showMainInter(Activity activity, OnAdClosedListener listener) {
            mOnAdClosedListener = listener;

            int position = PowerPreference.getDefaultFile().getInt(Constant.INTER_POSITION);
            PowerPreference.getDefaultFile().putInt(Constant.INTER_POSITION, position + 1);
            int position2 = PowerPreference.getDefaultFile().getInt(Constant.INTER_POSITION);

            if (position2 < MyApplication.arrayList.size()) {
                Log.e("TAG", "showMainInter " + MyApplication.arrayList.get(position2));
                switch (MyApplication.arrayList.get(position2)) {
                    case 0:
                        showGInter(activity, listener);
                        break;
                    case 1:
                        showApInter(activity, listener);
                        break;
                    default:
                        showMainInter(activity, listener);
                        break;
                }
            } else {
                loadInter(activity);

                if (listener != null)
                    listener.onAdClosed();
            }
    }


    private void showGInter(Activity activity, OnAdClosedListener listener) {
        Log.e("TAG", "showGInter");
        if (isGinterloaded && gInterstitialAd != null) {
            ShowProgress(activity);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    HideProgress(activity);
                    BannerAds.adCheck = true;
                    int appGCount = PowerPreference.getDefaultFile().getInt(Constant.APP_INTERVAL_COUNT);
                    appGCount++;
                    PowerPreference.getDefaultFile().putInt(Constant.APP_INTERVAL_COUNT, appGCount);
                    gInterstitialAd.show(activity);
                }
            }, 1000);
        } else {
            showMainInter(activity, listener);
        }
    }

    private void showApInter(Activity activity, OnAdClosedListener listener) {
        Log.e("TAG", "showApInter");
        if (isApinterloaded && Appodeal.isInitialized(Appodeal.INTERSTITIAL)) {
            ShowProgress(activity);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    HideProgress(activity);
                    BannerAds.adCheck = true;
                    int appGCount = PowerPreference.getDefaultFile().getInt(Constant.APP_INTERVAL_COUNT);
                    appGCount++;
                    PowerPreference.getDefaultFile().putInt(Constant.APP_INTERVAL_COUNT, appGCount);
                    Appodeal.show(activity, Appodeal.INTERSTITIAL);
                }
            }, 1000);
        } else {
            showMainInter(activity, listener);

        }
    }


}
