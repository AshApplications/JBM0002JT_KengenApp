package com.google.gms.ads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.gms.ads.databinding.LayoutAdUniversalBinding;

import java.util.ArrayList;
import java.util.Collections;

public class AdLoader {

    private static final String KEY_INTERSTITIAL_INTERVAL_START_ACTIVITY = "KeyInterstitialStartActivity";
    private static final String KEY_INTERSTITIAL_INTERVAL_BACK_ACTIVITY = "KeyInterstitialBackActivity";
    private static final String KEY_FAILED_COUNT_INTERSTITIAL = "KeyFailedCountInterstitial";
    private static final String KEY_FAILED_COUNT_NATIVE = "KeyFailedCountNative";
    private static final String KEY_FAILED_COUNT_APP_OPEN = "KeyFailedCountAppOpen";
    private static final String KEY_FAILED_COUNT_BANNER = "KeyFailedCountBanner";
    private static AdLoader instance;
    public boolean isInterstitialLoading = false;
    public boolean isInterstitialShowing = false;
    private InterstitialAd interstitialAd = null;
    private NativeAd nativeAdPreload = null;
    private final ArrayList<NativeAd> nativeAds = new ArrayList<>();
    private static final int NATIVE_LIST_SIZE = 5;

    public static AdLoader getInstance() {
        if (instance == null) {
            instance = new AdLoader();
        }
        return instance;
    }

    public ArrayList<NativeAd> getNativeAds() {
        return nativeAds;
    }

    public static void disableAds() {
        MyApp.getAdModel().setAdsAppOpen("None");
        MyApp.getAdModel().setAdsBanner("None");
        MyApp.getAdModel().setAdsInterstitial("None");
        MyApp.getAdModel().setAdsInterstitialBack("None");
        MyApp.getAdModel().setAdsNative("None");

    }

    public static void resetCounter() {
        resetInterstitialInterval(true);
        resetInterstitialInterval(false);
        resetFailedCountInterstitial();
        resetFailedCountNative();
        resetFailedCountAppOpen();
        resetFailedCountBanner();
    }

    private static void resetInterstitialInterval(boolean isBack) {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(isBack ? KEY_INTERSTITIAL_INTERVAL_BACK_ACTIVITY : KEY_INTERSTITIAL_INTERVAL_START_ACTIVITY, 0).apply();
    }


    private static void increaseInterstitialInterval(boolean isBack) {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(isBack ? KEY_INTERSTITIAL_INTERVAL_BACK_ACTIVITY : KEY_INTERSTITIAL_INTERVAL_START_ACTIVITY, getInterstitialInterval(isBack) + 1).apply();
    }

    private static int getInterstitialInterval(boolean isBack) {
        SharedPreferences preferences = getPreference();
        return preferences.getInt(isBack ? KEY_INTERSTITIAL_INTERVAL_BACK_ACTIVITY : KEY_INTERSTITIAL_INTERVAL_START_ACTIVITY, 0);
    }

    private static SharedPreferences getPreference() {
        return MyApp.getInstance().getSharedPreferences(MyApp.getInstance().getPackageName(), Context.MODE_PRIVATE);
    }

    private static void resetFailedCountInterstitial() {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(KEY_FAILED_COUNT_INTERSTITIAL, 0).apply();
    }

    private static void resetFailedCountNative() {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(KEY_FAILED_COUNT_NATIVE, 0).apply();
    }

    public static void resetFailedCountAppOpen() {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(KEY_FAILED_COUNT_APP_OPEN, 0).apply();
    }

    public static void resetFailedCountBanner() {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(KEY_FAILED_COUNT_BANNER, 0).apply();
    }


    private static void increaseFailedCountInterstitial() {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(KEY_FAILED_COUNT_INTERSTITIAL, getFailedCountInterstitial() + 1).apply();
    }

    private static void increaseFailedCountNative() {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(KEY_FAILED_COUNT_NATIVE, getFailedCountNative() + 1).apply();
    }

    public static void increaseFailedCountAppOpen() {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(KEY_FAILED_COUNT_APP_OPEN, getFailedCountAppOpen() + 1).apply();
    }

    public static void increaseFailedCountBanner() {
        SharedPreferences preferences = getPreference();
        preferences.edit().putInt(KEY_FAILED_COUNT_BANNER, getFailedCountBanner() + 1).apply();
    }

    private static int getFailedCountInterstitial() {
        SharedPreferences preferences = getPreference();
        return preferences.getInt(KEY_FAILED_COUNT_INTERSTITIAL, 0);
    }

    private static int getFailedCountNative() {
        SharedPreferences preferences = getPreference();
        return preferences.getInt(KEY_FAILED_COUNT_NATIVE, 0);
    }

    public static int getFailedCountAppOpen() {
        SharedPreferences preferences = getPreference();
        return preferences.getInt(KEY_FAILED_COUNT_APP_OPEN, 0);
    }

    public static int getFailedCountBanner() {
        SharedPreferences preferences = getPreference();
        return preferences.getInt(KEY_FAILED_COUNT_BANNER, 0);
    }

    /*public static void showExit(Activity activity) {
        if (ArMyApp.getAdModel().getAdsNative().equalsIgnoreCase("Yes") && ArMyApp.getAdModel().getAdsExit().equalsIgnoreCase("Yes")) {
            showExitBottomDialog(activity);
        } else {
            showExitDialog(activity);
        }
    }

    private static void showExitBottomDialog(Activity activity) {
        try {
            BottomSheetDialog dialog = new BottomSheetDialog(activity, androidx.appcompat.R.style.Base_Theme_AppCompat_Light_Dialog);
            DialogArExitBottomBinding binding = DialogArExitBottomBinding.inflate(LayoutInflater.from(activity), null, false);
            dialog.setContentView(binding.getRoot());
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            dialog.setOnShowListener(dialog1 -> ArAdLoaderAr.getInstance().showNativeLarge(activity, binding.ltUniversal));
            binding.cvExit.setOnClickListener(v -> {
                dialog.setOnDismissListener(dialog12 -> activity.finishAffinity());
                dialog.dismiss();
            });
            if (ArMyApp.getAdModel().getQurekaLink() == null || ArMyApp.getAdModel().getQurekaLink().trim().equals("")) {
                binding.btnPlayGame.setVisibility(View.GONE);
            } else {
                binding.btnPlayGame.setVisibility(View.VISIBLE);
                Animation anim = new ScaleAnimation(0.9f, 1F, // Start and end values for the X axis scaling
                        0.9F, 1F, // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setDuration(1000); //You can manage the blinking time with this parameter
                anim.setStartOffset(20);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setFillAfter(true);
                anim.setRepeatCount(Animation.INFINITE);
                binding.btnPlayGame.startAnimation(anim);
                binding.btnPlayGame.setOnClickListener(v -> {
                    dialog.setOnDismissListener(dialog13 -> openUrl(activity, ArMyApp.getAdModel().getQurekaLink()));
                    dialog.dismiss();
                });
            }
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*public static void showExitDialog(Activity activity) {
        try {
            Dialog dialog = new Dialog(activity);
            DialogArExitBinding binding = DialogArExitBinding.inflate(LayoutInflater.from(activity), null, false);
            dialog.setContentView(binding.getRoot());
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            dialog.show();
            binding.btnYes.setOnClickListener(v -> {
                dialog.setOnDismissListener(dialog1 -> activity.finishAffinity());
                dialog.dismiss();
            });
            binding.btnNo.setOnClickListener(v -> dialog.dismiss());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static void startActivityWithAd(Activity activity, Intent intent) {
        AdLoader.getInstance().showInterstitialAd(activity, false, () -> activity.startActivity(intent));
    }

    public static void showAds(Activity activity, FullScreenDismissListener listener) {
        AdLoader.getInstance().showInterstitialAd(activity, false, listener);
    }

    public static void startActivityWithAd(Activity activity, ActivityResultLauncher<Intent> launcherIntent, Intent intent) {
        AdLoader.getInstance().showInterstitialAd(activity, false, () -> launcherIntent.launch(intent));
    }

    public static void startActivityWithFinishAd(Activity activity, Intent intent) {
        AdLoader.getInstance().showInterstitialAd(activity, false, () -> {
            activity.startActivity(intent);
            activity.finish();
        });
    }

    public static void startActivityWithFinishAffinityAd(Activity activity, Intent intent) {
        AdLoader.getInstance().showInterstitialAd(activity, false, () -> {
            activity.startActivity(intent);
            activity.finishAffinity();
        });
    }

    public static void finishWithAd(Activity activity) {
        AdLoader.getInstance().showInterstitialAd(activity, true, activity::finish);
    }

    public static void finishWithResultAd(Activity activity, int resultCode, Intent result) {
        AdLoader.getInstance().showInterstitialAd(activity, true, () -> {
            activity.setResult(resultCode, result);
            activity.finish();
        });
    }

    public static void onBackPressedWithAd(Activity activity, FullScreenDismissListener listener) {
        AdLoader.getInstance().showInterstitialAd(activity, true, listener);
    }

    public static void finishAffinityWithAd(Activity activity) {
        AdLoader.getInstance().showInterstitialAd(activity, true, activity::finishAffinity);
    }

    public static void openUrl(Activity activity, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void rateUs(Activity activity) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + activity.getPackageName()));
            activity.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName()));
                activity.startActivity(intent);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void shareApp(Activity activity, String appTitle, String appPackage) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, appTitle);
            String shareMessage = "Let me recommend you this application";
            shareMessage = (shareMessage + " https://play.google.com/store/apps/details?id=" + appPackage);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            activity.startActivity(Intent.createChooser(shareIntent, "Select App"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void log(String log, Exception e) {
        Log.d("ADCBLOGICLOG", log, e);
    }

    public static void log(String log) {
        Log.d("ADCBLOGICLOG", log);
    }

    public void loadInterstitialAds(final Activity activity) {
        if (MyApp.getAdModel().getAdsInterstitial().equalsIgnoreCase("Google")) {
            loadInterstitialAd(activity);
        } else if (MyApp.getAdModel().getAdsInterstitialBack().equalsIgnoreCase("Google")) {
            loadInterstitialAd(activity);
        }
    }

    private void showBanner(final Activity activity, final LayoutAdUniversalBinding ltUniversal) {
        if (getFailedCountBanner() < MyApp.getAdModel().getAdsBannerFailedCount() && MyApp.getAdModel().getAdsBanner().equalsIgnoreCase("Google")) {
            //remove any margin if exist
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) ltUniversal.getRoot().getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            ltUniversal.getRoot().requestLayout();

            //Remove Border
            ltUniversal.getRoot().setStrokeWidth(0);

            //Set flAd View Height to Same As Place Holder
            ltUniversal.flAd.getLayoutParams().height = ltUniversal.tvAdSpaceBanner.getLayoutParams().height;

            AdView adView = new AdView(activity);
            adView.setAdUnitId(MyApp.getAdModel().getAdsBannerId());
//            AdSize adSize = getAdSize(activity);
            adView.setAdSize(AdSize.BANNER);
            AdRequest adRequest = new AdRequest.Builder().build();
            ltUniversal.tvAdSpaceBanner.setVisibility(View.VISIBLE);
            ltUniversal.tvAdSpaceLarge.setVisibility(View.GONE);
            ltUniversal.tvAdSpaceSmall.setVisibility(View.GONE);
            ltUniversal.flAd.setVisibility(View.GONE);
            log("BANNER -> AD REQUEST");
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    log("BANNER -> AD LOADED");
                    log("BANNER -> AD SHOW");
                    ltUniversal.tvAdSpaceBanner.setVisibility(View.GONE);
                    ltUniversal.tvAdSpaceLarge.setVisibility(View.GONE);
                    ltUniversal.tvAdSpaceSmall.setVisibility(View.GONE);
                    ltUniversal.flAd.setVisibility(View.VISIBLE);
                    ltUniversal.flAd.removeAllViews();
                    ltUniversal.flAd.addView(adView);
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    showBanner(activity, ltUniversal);
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    log("BANNER -> AD FAILED");
                    ltUniversal.flAd.setVisibility(View.GONE);
                }
            });
        } else {
            ltUniversal.tvAdSpaceBanner.setVisibility(View.GONE);
            ltUniversal.tvAdSpaceLarge.setVisibility(View.GONE);
            ltUniversal.tvAdSpaceSmall.setVisibility(View.GONE);
            ltUniversal.flAd.setVisibility(View.GONE);
        }
    }

    private void loadInterstitialAd(final Activity activity) {
        if (getFailedCountInterstitial() < MyApp.getAdModel().getAdsInterstitialFailedCount()) {
            if (interstitialAd == null && !isInterstitialLoading) {
                isInterstitialLoading = true;
                AdRequest adRequest = new AdRequest.Builder().build();
                log("INTERSTITIAL -> AD REQUEST");
                InterstitialAd.load(activity, MyApp.getAdModel().getAdsInterstitialId(), adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        log("INTERSTITIAL -> AD LOADED");
                        AdLoader.this.interstitialAd = interstitialAd;
                        resetFailedCountInterstitial();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        increaseFailedCountInterstitial();
                        log("INTERSTITIAL -> AD FAILED (" + getFailedCountInterstitial() + " of " + MyApp.getAdModel().getAdsInterstitialFailedCount() + ")\nKEY: " + MyApp.getAdModel().getAdsInterstitialId() + "ERROR: " + loadAdError.getMessage());
                        interstitialAd = null;
                        isInterstitialLoading = false;
                    }
                });
            }
        } else {
            log("INTERSTITIAL -> FAILED COUNTER IS " + MyApp.getAdModel().getAdsInterstitialFailedCount());
        }
    }

    public void showInterstitialAd(Activity activity, boolean isBack, FullScreenDismissListener listener) {
        if (isBack ? MyApp.getAdModel().getAdsInterstitialBack().equalsIgnoreCase("Google") : MyApp.getAdModel().getAdsInterstitial().equalsIgnoreCase("Google")) {
            int currentInterval = getInterstitialInterval(isBack);
            int regularInterval = isBack ? MyApp.getAdModel().getAdsInterstitialBackCount() : MyApp.getAdModel().getAdsInterstitialCount();
            if (currentInterval == regularInterval) {
                if (interstitialAd != null) {
                    interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            resetInterstitialInterval(isBack);
                            interstitialAd = null;
                            isInterstitialLoading = false;
                            isInterstitialShowing = false;
                            loadInterstitialAd(activity);
                            listener.onDismiss();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            interstitialAd = null;
                            isInterstitialLoading = false;
                            isInterstitialShowing = false;
                            listener.onDismiss();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            interstitialAd = null;
                            isInterstitialShowing = true;
                        }
                    });
                    AdLoader.log("INTERSTITIAL -> AD SHOW");
                    interstitialAd.show(activity);
                } else {
                    loadInterstitialAds(activity);
                    listener.onDismiss();
                }
            } else {
                increaseInterstitialInterval(isBack);
                listener.onDismiss();
            }
        } else {
            listener.onDismiss();
        }
    }

    private AdSize getAdSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    private void showNativeLarge(Activity activity, LayoutAdUniversalBinding ltUniversal) {
        showNative(activity, ltUniversal, "Large");
    }

    private void showNativeSmall(Activity activity, LayoutAdUniversalBinding ltUniversal) {
        showNative(activity, ltUniversal, "Small");
    }

    public void showNativeLargeList(Activity activity, LayoutAdUniversalBinding ltNative) {
        showNativeList(activity, ltNative, "Large");
    }

    public void showNativeSmallList(Activity activity, LayoutAdUniversalBinding ltNative) {
        showNativeList(activity, ltNative, "Small");
    }

    public void showNativeList(Activity activity, LayoutAdUniversalBinding ltNative, String adType) {
        if (nativeAds.size() > 0) {
            Collections.shuffle(nativeAds);
            NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_large, null, false);
            if (adType.equalsIgnoreCase("Small")) {
                if (MyApp.getAdModel().getAdsNativeViewId() == 1) {
                    adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_small_1, null, false);
                } else if (MyApp.getAdModel().getAdsNativeViewId() == 2) {
                    adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_small_2, null, false);
                }
            } else if (adType.equalsIgnoreCase("Large")) {
                adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_large, null, false);
            }
            AdLoader.getInstance().inflateGoogleNativeAd(activity, nativeAds.get(0), adView);
            ltNative.flAd.removeAllViews();
            ltNative.flAd.addView(adView);
        } else {
            ltNative.flAd.removeAllViews();
            ltNative.cvAdMain.setVisibility(View.GONE);
        }
    }

    public void showUniversalAd(Activity activity, LayoutAdUniversalBinding ltUniversal, boolean showBanner) {
        if (MyApp.getAdModel().getAdsBottomLayout() == 1) {
            if (showBanner) {
                showBanner(activity, ltUniversal);
            } else {
                showNativeSmall(activity, ltUniversal);
            }
        } else if (MyApp.getAdModel().getAdsBottomLayout() == 2) {
            showBanner(activity, ltUniversal);
        } else if (MyApp.getAdModel().getAdsBottomLayout() == 3) {
            showNativeSmall(activity, ltUniversal);
        }
    }

    private void showNative(Activity activity, LayoutAdUniversalBinding ltUniversal, String adType) {
        if (MyApp.getAdModel().getAdsNative().equalsIgnoreCase("Google")) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) ltUniversal.getRoot().getLayoutParams();
            int margin = (int) activity.getResources().getDimension(com.intuit.sdp.R.dimen._3sdp);
            layoutParams.setMargins(margin, margin, margin, margin);
            ltUniversal.getRoot().requestLayout();
            if (nativeAdPreload != null) {
                showNativeAd(activity, ltUniversal, adType);
            } else {
                loadNativeAd(activity, ltUniversal, adType);
            }
        } else {
            ltUniversal.tvAdSpaceBanner.setVisibility(View.GONE);
            ltUniversal.tvAdSpaceLarge.setVisibility(View.GONE);
            ltUniversal.tvAdSpaceSmall.setVisibility(View.GONE);
            ltUniversal.flAd.setVisibility(View.GONE);
        }
    }

    private void showNativeAd(Activity activity, LayoutAdUniversalBinding ltUniversal, String adType) {
        if (nativeAdPreload != null) {
            AdLoader.log("NATIVE (PRELOAD) -> AD SHOW");
            NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_small_1, null, false);
            if (adType.equalsIgnoreCase("Small")) {
                if (MyApp.getAdModel().getAdsNativeViewId() == 1) {
                    adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_small_1, null, false);
                } else if (MyApp.getAdModel().getAdsNativeViewId() == 2) {
                    adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_small_2, null, false);
                }
            } else if (adType.equalsIgnoreCase("Large")) {
                adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_large, null, false);
            }

            AdLoader.getInstance().inflateGoogleNativeAd(activity, nativeAdPreload, adView);
            ltUniversal.tvAdSpaceBanner.setVisibility(View.GONE);
            ltUniversal.tvAdSpaceLarge.setVisibility(View.GONE);
            ltUniversal.tvAdSpaceSmall.setVisibility(View.GONE);
            ltUniversal.flAd.setVisibility(View.VISIBLE);
            ltUniversal.flAd.removeAllViews();
            ltUniversal.flAd.addView(adView);
            loadNativeAdPreload(activity);
        } else {
            loadNativeAd(activity, ltUniversal, adType);
        }
    }

    public void loadNativeAdPreload(Activity activity) {
        if (getFailedCountNative() < MyApp.getAdModel().getAdsNativeFailedCount()) {
            if (MyApp.getAdModel().getAdsNativePreload().equalsIgnoreCase("Yes") && MyApp.getAdModel().getAdsNative().equalsIgnoreCase("Google")) {
                nativeAdPreload = null;
                VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
                NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
                com.google.android.gms.ads.AdLoader appLoaderNativeOne = new com.google.android.gms.ads.AdLoader.Builder(activity, MyApp.getAdModel().getAdsNativeId()).forNativeAd(nativeAd -> {
                    log("NATIVE (PRELOAD) -> AD LOADED");
                    this.nativeAdPreload = nativeAd;
                    if (nativeAds.size() < NATIVE_LIST_SIZE) {
                        nativeAds.add(nativeAd);
                    }
                }).withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        increaseFailedCountNative();
                        log("NATIVE (PRELOAD) -> AD FAILED (" + getFailedCountNative() + " of " + MyApp.getAdModel().getAdsNativeFailedCount() + ")\nKEY: " + MyApp.getAdModel().getAdsNativeId() + "ERROR: " + adError.getMessage());
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }
                }).withNativeAdOptions(adOptions).build();

                AdRequest adRequest = new AdRequest.Builder().build();
                log("NATIVE (PRELOAD) -> AD REQUEST");
                appLoaderNativeOne.loadAd(adRequest);
            }
        } else {
            log("NATIVE (PRELOAD) -> FAILED COUNTER IS " + MyApp.getAdModel().getAdsNativeFailedCount());
        }
    }

    private void loadNativeAd(Activity activity, LayoutAdUniversalBinding ltUniversal, String adType) {
        if (adType.equalsIgnoreCase("Small")) {
            ltUniversal.tvAdSpaceBanner.setVisibility(View.GONE);
            ltUniversal.tvAdSpaceLarge.setVisibility(View.GONE);
            ltUniversal.tvAdSpaceSmall.setVisibility(View.VISIBLE);
            ltUniversal.flAd.setVisibility(View.GONE);
        } else if (adType.equalsIgnoreCase("Large")) {
            ltUniversal.tvAdSpaceBanner.setVisibility(View.GONE);
            ltUniversal.tvAdSpaceLarge.setVisibility(View.VISIBLE);
            ltUniversal.tvAdSpaceSmall.setVisibility(View.GONE);
            ltUniversal.flAd.setVisibility(View.GONE);
        }
        if (getFailedCountNative() < MyApp.getAdModel().getAdsNativeFailedCount()) {
            VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
            NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
            com.google.android.gms.ads.AdLoader adLoaderNative = new com.google.android.gms.ads.AdLoader.Builder(activity, MyApp.getAdModel().getAdsNativeId()).forNativeAd(nativeAd -> {
                log("NATIVE -> AD LOADED");
                resetFailedCountNative();
                AdLoader.log("NATIVE -> AD SHOW");
                NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_small_1, null, false);
                if (adType.equalsIgnoreCase("Small")) {
                    if (MyApp.getAdModel().getAdsNativeViewId() == 1) {
                        adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_small_1, null, false);
                    } else if (MyApp.getAdModel().getAdsNativeViewId() == 2) {
                        adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_small_2, null, false);
                    }
                } else if (adType.equalsIgnoreCase("Large")) {
                    adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_google_native_large, null, false);
                }
                AdLoader.getInstance().inflateGoogleNativeAd(activity, nativeAd, adView);
                ltUniversal.tvAdSpaceBanner.setVisibility(View.GONE);
                ltUniversal.tvAdSpaceLarge.setVisibility(View.GONE);
                ltUniversal.tvAdSpaceSmall.setVisibility(View.GONE);
                ltUniversal.flAd.setVisibility(View.VISIBLE);
                ltUniversal.flAd.removeAllViews();
                ltUniversal.flAd.addView(adView);
                loadNativeAdPreload(activity);
            }).withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                    increaseFailedCountNative();
                    log("NATIVE -> AD FAILED (" + getFailedCountNative() + " of " + MyApp.getAdModel().getAdsNativeFailedCount() + ")\nKEY: " + MyApp.getAdModel().getAdsInterstitialId() + "ERROR: " + adError.getMessage());
                    ltUniversal.flAd.setVisibility(View.GONE);
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    showNative(activity, ltUniversal, adType);
                }
            }).withNativeAdOptions(adOptions).build();

            AdRequest adRequest = new AdRequest.Builder().build();
            log("NATIVE -> AD REQUEST");
            adLoaderNative.loadAd(adRequest);
        } else {
            ltUniversal.flAd.setVisibility(View.GONE);
            log("NATIVE -> FAILED COUNTER IS " + MyApp.getAdModel().getAdsNativeFailedCount());
        }
    }

    private void inflateGoogleNativeAd(Activity activity, NativeAd nativeAd, NativeAdView adView) {
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        //adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        AppCompatButton install = adView.findViewById(R.id.ad_call_to_action);
        install.setText(nativeAd.getCallToAction());

        install.getBackground().setColorFilter(Color.parseColor(MyApp.getAdModel().getAdsNativeColor()), PorterDuff.Mode.SRC_ATOP);

        adView.setCallToActionView(install);

        if (adView.getHeadlineView() != null) {
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        }

        if (adView.getMediaView() != null) {
            adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
        }

        if (adView.getBodyView() != null) {
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.GONE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }
        }

        if (adView.getCallToActionView() != null) {
            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.GONE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }
        }

        if (adView.getIconView() != null) {
            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
                CardView ad_app_icon_cards = adView.findViewById(R.id.ad_app_icon_cards);
                View vSpace = adView.findViewById(R.id.vSpace);
                vSpace.setVisibility(View.VISIBLE);
                ad_app_icon_cards.setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
                View vSpace = adView.findViewById(R.id.vSpace);
                vSpace.setVisibility(View.GONE);
                CardView ad_app_icon_cards = adView.findViewById(R.id.ad_app_icon_cards);
                ad_app_icon_cards.setVisibility(View.VISIBLE);
            }
        }

        if (adView.getPriceView() != null) {
            if (nativeAd.getPrice() == null) {
                adView.getPriceView().setVisibility(View.GONE);
            } else {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
            }
        }

        if (adView.getStoreView() != null) {
            if (nativeAd.getStore() == null) {
                adView.getStoreView().setVisibility(View.GONE);
            } else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            }
        }

        if (adView.getStarRatingView() != null) {
            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.GONE);
            } else {
                ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }
        }

        if (adView.getAdvertiserView() != null) {
            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.GONE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }
        }

        adView.setNativeAd(nativeAd);

        if (nativeAd.getMediaContent() != null && nativeAd.getMediaContent().hasVideoContent()) {
            VideoController vc = nativeAd.getMediaContent().getVideoController();
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        }
    }

    public interface FullScreenDismissListener {
        void onDismiss();
    }

    public interface NoInternetConnectionRetryListener {
        void onRetry();
    }
}
