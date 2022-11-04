package com.google.gms.ads;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.preference.PowerPreference;

public class MainAds {

    public void loadAds(Activity activity) {

        if (PowerPreference.getDefaultFile().getBoolean(AdUtils.AdsOnOff, true)) {

            if (PowerPreference.getDefaultFile().getInt(AdUtils.SERVER_INTERVAL_COUNT, 0) > 0)
                new InterAds().loadInterAds(activity);

            if (PowerPreference.getDefaultFile().getInt(AdUtils.SERVER_BACK_COUNT, 0) > 0)
                new BackInterAds().loadInterAds(activity);

            if (PowerPreference.getDefaultFile().getInt(AdUtils.AppOpenTime, 0) > 0)
                new OpenAds().loadOpenAd();

            loadBannerAds(activity);
            loadNativeAds(activity);
            loadListNativeAds(activity);
        }
    }

    // INTER ADS

    public void showSplashInterAds(Activity activity, InterAds.OnAdClosedListener listener) {
        if (PowerPreference.getDefaultFile().getBoolean(AdUtils.AdsOnOff, true)) {
            PowerPreference.getDefaultFile().putInt(AdUtils.APP_INTERVAL_COUNT, 0);
            new InterAds().showSplashAds(activity, listener);
        } else {
            listener.onAdClosed();
        }
    }

    public void showInterAds(Activity activity, InterAds.OnAdClosedListener listener) {
        if (PowerPreference.getDefaultFile().getBoolean(AdUtils.AdsOnOff, true) && PowerPreference.getDefaultFile().getInt(AdUtils.SERVER_INTERVAL_COUNT, 0) > 0) {
            new InterAds().showInterAds(activity, listener);
        } else {
            listener.onAdClosed();
        }
    }

    public void showBackInterAds(Activity activity, BackInterAds.OnAdClosedListener listener) {
        if (PowerPreference.getDefaultFile().getBoolean(AdUtils.AdsOnOff, true) && PowerPreference.getDefaultFile().getInt(AdUtils.SERVER_BACK_COUNT, 0) > 0) {
            new BackInterAds().showInterAds(activity, listener);
        } else {
            listener.onAdClosed();
        }
    }


    // OPEN ADS

    public void showOpenAds(Activity activity, OpenAds.OnAdClosedListener listener) {
        if (PowerPreference.getDefaultFile().getBoolean(AdUtils.AdsOnOff, true)) {
            if (PowerPreference.getDefaultFile().getInt(AdUtils.AppOpenTime, 0) == 1) {
                if (!PowerPreference.getDefaultFile().getBoolean(AdUtils.APP_OPEN_SHOW, false)) {
                    new OpenAds().showOpenAd(activity, listener);
                } else {
                    if (listener != null) {
                        listener.onAdClosed();
                    }
                }
            } else if (PowerPreference.getDefaultFile().getInt(AdUtils.AppOpenTime, 0) == 2) {
                new OpenAds().showOpenAd(activity, listener);
            } else if (PowerPreference.getDefaultFile().getInt(AdUtils.AppOpenTime, 0) == 3) {
                if (listener != null) {
                    new OpenAds().showOpenAd(activity, listener);
                }
            } else {
                if (listener != null) {
                    listener.onAdClosed();
                }
            }
        } else {
            if (listener != null) {
                listener.onAdClosed();
            }
        }
    }

    // BANNER ADS

    public void loadBannerAds(Activity activity) {
        if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneBannerNative, 0) == 1) {
            new MiniNativeAds().loadNativeAds(activity);
        }
    }

    public void showBannerAds(Activity activity, LinearLayout adLayout, TextView adSpace) {

        if (adLayout == null || adSpace == null) {
            adLayout = activity.findViewById(R.id.adFrameMini);
            adSpace = activity.findViewById(R.id.adSpaceMini);
        }

        if (adLayout == null || adSpace == null)
            return;

        if (PowerPreference.getDefaultFile().getBoolean(AdUtils.AdsOnOff, true)) {
            if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneBannerNative, 0) == 1) {
                new MiniNativeAds().showNativeAds(activity, adLayout, adSpace);
            } else if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneBannerNative, 0) == 2) {
                new MiniBannerAds().loadBannerAds(activity, adLayout, adSpace);
            } else {
                adLayout.removeAllViews();
            }
        } else {
            adLayout.removeAllViews();
        }
    }

    // LARGE NATIVE ADS

    public void loadNativeAds(Activity activity) {
        if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneAllNative, 0) > 0) {
            new LargeNativeAds().loadNativeAds(activity);
        }
    }

    public LinearLayout getFrameLayout(Activity activity, Dialog dialog) {
        if (dialog != null) {
            return dialog.findViewById(R.id.adFrameLarge);
        } else {
            return activity.findViewById(R.id.adFrameLarge);
        }
    }


    public TextView getTextLayout(Activity activity, Dialog dialog) {
        if (dialog != null) {
            return dialog.findViewById(R.id.adSpaceLarge);
        } else {
            return activity.findViewById(R.id.adSpaceLarge);
        }
    }

    public void showNativeAds(Activity activity, Dialog dialog, LinearLayout adLayout, TextView
            adSpace) {

        if (adLayout == null || adSpace == null) {
            adLayout = getFrameLayout(activity, dialog);
            adSpace = getTextLayout(activity, dialog);
        }

        if (adLayout == null || adSpace == null)
            return;

        if (PowerPreference.getDefaultFile().getBoolean(AdUtils.AdsOnOff, true)) {
            if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneAllNative, 0) > 0) {
                new LargeNativeAds().showNativeAds(activity, adLayout, adSpace);
            } else {
                adLayout.removeAllViews();
            }
        } else {
            adLayout.removeAllViews();
        }
    }

    // LIST NATIVE ADS

    public void loadListNativeAds(Activity activity) {
        if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneListNative, 0) > 0) {
            new ListNativeAds().loadNativeAds(activity);
        }
    }

    public void showListNativeAds(Activity activity, LinearLayout adLayout, TextView adSpace) {

        if (PowerPreference.getDefaultFile().getBoolean(AdUtils.AdsOnOff, true)) {
            if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneListNative, 0) > 0) {
                new ListNativeAds().showListNativeAds(activity, adLayout, adSpace);
            } else {
                adLayout.removeAllViews();
            }
        } else {
            adLayout.removeAllViews();
        }
    }

}
