package com.google.gms.ads.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdModel {

    @SerializedName("ads_app_id")
    @Expose
    private String adsAppId = "None";

    @SerializedName("ads_app_open_id")
    @Expose
    private String adsAppOpenId = "None";

    @SerializedName("ads_interstitial_id")
    @Expose
    private String adsInterstitialId = "None";

    @SerializedName("ads_banner_id")
    @Expose
    private String adsBannerId = "None";

    @SerializedName("ads_native_id")
    @Expose
    private String adsNativeId = "None";



    @SerializedName("ads_splash")
    @Expose
    private String adsSplash = "None";

    @SerializedName("ads_exit")
    @Expose
    private String adsExit = "None";



    @SerializedName("ads_banner")
    @Expose
    private String adsBanner = "Google";

    @SerializedName("ads_interstitial")
    @Expose
    private String adsInterstitial = "Google";

    @SerializedName("ads_interstitial_back")
    @Expose
    private String adsInterstitialBack = "Google";

    @SerializedName("ads_native")
    @Expose
    private String adsNative = "Google";

    @SerializedName("ads_app_open")
    @Expose
    private String adsAppOpen = "Google";

    @SerializedName("app_open_back")
    @Expose
    private String appOpenBack = "None";

    @SerializedName("ads_on_off")
    @Expose
    private String adsOnOff = "Yes";



    @SerializedName("ads_interstitial_count")
    @Expose
    private int adsInterstitialCount = 3;

    @SerializedName("ads_interstitial_back_count")
    @Expose
    private int adsInterstitialBackCount = 3;


    @SerializedName("ads_interstitial_failed_count")
    @Expose
    private int adsInterstitialFailedCount = 3;

    @SerializedName("ads_native_failed_count")
    @Expose
    private int adsNativeFailedCount = 3;

    @SerializedName("ads_appopen_failed_count")
    @Expose
    private int adsAppOpenFailedCount = 3;

    @SerializedName("ads_banner_failed_count")
    @Expose
    private int adsBannerFailedCount = 3;


    @SerializedName("ads_native_preload")
    @Expose
    private String adsNativePreload = "None";

    @SerializedName("ads_native_dialog")
    @Expose
    private String adsNativeDialog = "None";


    @SerializedName("ads_list_view_count")
    @Expose
    private int adsListViewCount = 10;


    @SerializedName("ads_bottom_layout")
    @Expose
    private int adsBottomLayout = 1;


    public String getAppOpenBack() {
        return appOpenBack;
    }

    public void setAppOpenBack(String appOpenBack) {
        this.appOpenBack = appOpenBack;
    }

    public String getAdsOnOff() {
        return adsOnOff;
    }

    public void setAdsOnOff(String adsOnOff) {
        this.adsOnOff = adsOnOff;
    }


    public String getAdsNativeDialog() {
        return adsNativeDialog;
    }

    public void setAdsNativeDialog(String adsNativeDialog) {
        this.adsNativeDialog = adsNativeDialog;
    }



    public int getAdsBottomLayout() {
        return adsBottomLayout;
    }

    public void setAdsBottomLayout(int adsBottomLayout) {
        this.adsBottomLayout = adsBottomLayout;
    }

    public String getAdsAppId() {
        return adsAppId;
    }

    public void setAdsAppId(String adsAppId) {
        this.adsAppId = adsAppId;
    }

    public String getAdsAppOpenId() {
        return adsAppOpenId;
    }

    public void setAdsAppOpenId(String adsAppOpenId) {
        this.adsAppOpenId = adsAppOpenId;
    }

    public String getAdsInterstitialId() {
        return adsInterstitialId;
    }

    public void setAdsInterstitialId(String adsInterstitialId) {
        this.adsInterstitialId = adsInterstitialId;
    }

    public String getAdsBannerId() {
        return adsBannerId;
    }

    public void setAdsBannerId(String adsBannerId) {
        this.adsBannerId = adsBannerId;
    }

    public String getAdsNativeId() {
        return adsNativeId;
    }

    public void setAdsNativeId(String adsNativeId) {
        this.adsNativeId = adsNativeId;
    }

    public String getAdsSplash() {
        return adsSplash;
    }

    public void setAdsSplash(String adsSplash) {
        this.adsSplash = adsSplash;
    }

    public String getAdsExit() {
        return adsExit;
    }

    public void setAdsExit(String adsExit) {
        this.adsExit = adsExit;
    }

    public String getAdsBanner() {
        return adsBanner;
    }

    public void setAdsBanner(String adsBanner) {
        this.adsBanner = adsBanner;
    }

    public String getAdsInterstitial() {
        return adsInterstitial;
    }

    public void setAdsInterstitial(String adsInterstitial) {
        this.adsInterstitial = adsInterstitial;
    }

    public String getAdsInterstitialBack() {
        return adsInterstitialBack;
    }

    public void setAdsInterstitialBack(String adsInterstitialBack) {
        this.adsInterstitialBack = adsInterstitialBack;
    }

    public int getAdsListViewCount() {
        return adsListViewCount;
    }

    public void setAdsListViewCount(int adsListViewCount) {
        this.adsListViewCount = adsListViewCount;
    }

    public String getAdsNative() {
        return adsNative;
    }

    public void setAdsNative(String adsNative) {
        this.adsNative = adsNative;
    }

    public String getAdsAppOpen() {
        return adsAppOpen;
    }

    public void setAdsAppOpen(String adsAppOpen) {
        this.adsAppOpen = adsAppOpen;
    }

    public int getAdsInterstitialCount() {
        return adsInterstitialCount;
    }

    public void setAdsInterstitialCount(int adsInterstitialCount) {
        this.adsInterstitialCount = adsInterstitialCount;
    }

    public int getAdsInterstitialBackCount() {
        return adsInterstitialBackCount;
    }

    public void setAdsInterstitialBackCount(int adsInterstitialBackCount) {
        this.adsInterstitialBackCount = adsInterstitialBackCount;
    }

    public int getAdsInterstitialFailedCount() {
        return adsInterstitialFailedCount;
    }

    public void setAdsInterstitialFailedCount(int adsInterstitialFailedCount) {
        this.adsInterstitialFailedCount = adsInterstitialFailedCount;
    }

    public int getAdsNativeFailedCount() {
        return adsNativeFailedCount;
    }

    public void setAdsNativeFailedCount(int adsNativeFailedCount) {
        this.adsNativeFailedCount = adsNativeFailedCount;
    }

    public int getAdsAppOpenFailedCount() {
        return adsAppOpenFailedCount;
    }

    public void setAdsAppOpenFailedCount(int adsAppOpenFailedCount) {
        this.adsAppOpenFailedCount = adsAppOpenFailedCount;
    }

    public int getAdsBannerFailedCount() {
        return adsBannerFailedCount;
    }

    public void setAdsBannerFailedCount(int adsBannerFailedCount) {
        this.adsBannerFailedCount = adsBannerFailedCount;
    }

    public String getAdsNativePreload() {
        return adsNativePreload;
    }

    public void setAdsNativePreload(String adsNativePreload) {
        this.adsNativePreload = adsNativePreload;
    }

}