package com.water.alkaline.kengen.model.update;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdsInfo {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("GoogleBannerAds")
    @Expose
    private String googleBannerAds;
    @SerializedName("GoogleInterAds")
    @Expose
    private String googleInterAds;
    @SerializedName("GoogleNativeAds")
    @Expose
    private String googleNativeAds;
    @SerializedName("GoogleAppOpenAds")
    @Expose
    private String googleAppOpenAds;
    @SerializedName("GoogleAppIdAds")
    @Expose
    private String googleAppIdAds;
    @SerializedName("AdsOnOff")
    @Expose
    private Boolean adsOnOff;
    @SerializedName("GoogleAdsOnOff")
    @Expose
    private Boolean googleAdsOnOff;
    @SerializedName("QurekaOnOff")
    @Expose
    private Boolean qurekaOnOff;
    @SerializedName("AppOpenTime")
    @Expose
    private Integer appOpenTime;
    @SerializedName("WhichOneSplashAppOpen")
    @Expose
    private Integer whichOneSplashAppOpen;
    @SerializedName("WhichOneBannerNative")
    @Expose
    private Integer whichOneBannerNative;
    @SerializedName("WhichOneAllNative")
    @Expose
    private Integer whichOneAllNative;
    @SerializedName("WhichOneListNative")
    @Expose
    private Integer whichOneListNative;
    @SerializedName("ListNativeAfterCount")
    @Expose
    private Integer listNativeAfterCount;
    @SerializedName("LoaderNativeOnOff")
    @Expose
    private Boolean loaderNativeOnOff;
    @SerializedName("InterIntervalCount")
    @Expose
    private Integer interIntervalCount;
    @SerializedName("BackInterIntervalCount")
    @Expose
    private Integer backInterIntervalCount;
    @SerializedName("ShowDialogBeforeAds")
    @Expose
    private Boolean showDialogBeforeAds;
    @SerializedName("DialogTimeInSec")
    @Expose
    private Integer dialogTimeInSec;
    @SerializedName("adsCloseCount")
    @Expose
    private Integer adsCloseCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoogleBannerAds() {
        return googleBannerAds;
    }

    public void setGoogleBannerAds(String googleBannerAds) {
        this.googleBannerAds = googleBannerAds;
    }

    public String getGoogleInterAds() {
        return googleInterAds;
    }

    public void setGoogleInterAds(String googleInterAds) {
        this.googleInterAds = googleInterAds;
    }

    public String getGoogleNativeAds() {
        return googleNativeAds;
    }

    public void setGoogleNativeAds(String googleNativeAds) {
        this.googleNativeAds = googleNativeAds;
    }

    public String getGoogleAppOpenAds() {
        return googleAppOpenAds;
    }

    public void setGoogleAppOpenAds(String googleAppOpenAds) {
        this.googleAppOpenAds = googleAppOpenAds;
    }

    public String getGoogleAppIdAds() {
        return googleAppIdAds;
    }

    public void setGoogleAppIdAds(String googleAppIdAds) {
        this.googleAppIdAds = googleAppIdAds;
    }

    public Boolean getAdsOnOff() {
        return adsOnOff;
    }

    public void setAdsOnOff(Boolean adsOnOff) {
        this.adsOnOff = adsOnOff;
    }

    public Boolean getGoogleAdsOnOff() {
        return googleAdsOnOff;
    }

    public void setGoogleAdsOnOff(Boolean googleAdsOnOff) {
        this.googleAdsOnOff = googleAdsOnOff;
    }

    public Boolean getQurekaOnOff() {
        return qurekaOnOff;
    }

    public void setQurekaOnOff(Boolean qurekaOnOff) {
        this.qurekaOnOff = qurekaOnOff;
    }

    public Integer getAppOpenTime() {
        return appOpenTime;
    }

    public void setAppOpenTime(Integer appOpenTime) {
        this.appOpenTime = appOpenTime;
    }

    public Integer getWhichOneSplashAppOpen() {
        return whichOneSplashAppOpen;
    }

    public void setWhichOneSplashAppOpen(Integer whichOneSplashAppOpen) {
        this.whichOneSplashAppOpen = whichOneSplashAppOpen;
    }

    public Integer getWhichOneBannerNative() {
        return whichOneBannerNative;
    }

    public void setWhichOneBannerNative(Integer whichOneBannerNative) {
        this.whichOneBannerNative = whichOneBannerNative;
    }

    public Integer getWhichOneAllNative() {
        return whichOneAllNative;
    }

    public void setWhichOneAllNative(Integer whichOneAllNative) {
        this.whichOneAllNative = whichOneAllNative;
    }

    public Integer getWhichOneListNative() {
        return whichOneListNative;
    }

    public void setWhichOneListNative(Integer whichOneListNative) {
        this.whichOneListNative = whichOneListNative;
    }

    public Integer getListNativeAfterCount() {
        return listNativeAfterCount;
    }

    public void setListNativeAfterCount(Integer listNativeAfterCount) {
        this.listNativeAfterCount = listNativeAfterCount;
    }

    public Boolean getLoaderNativeOnOff() {
        return loaderNativeOnOff;
    }

    public void setLoaderNativeOnOff(Boolean loaderNativeOnOff) {
        this.loaderNativeOnOff = loaderNativeOnOff;
    }

    public Integer getInterIntervalCount() {
        return interIntervalCount;
    }

    public void setInterIntervalCount(Integer interIntervalCount) {
        this.interIntervalCount = interIntervalCount;
    }

    public Integer getBackInterIntervalCount() {
        return backInterIntervalCount;
    }

    public void setBackInterIntervalCount(Integer backInterIntervalCount) {
        this.backInterIntervalCount = backInterIntervalCount;
    }

    public Boolean getShowDialogBeforeAds() {
        return showDialogBeforeAds;
    }

    public void setShowDialogBeforeAds(Boolean showDialogBeforeAds) {
        this.showDialogBeforeAds = showDialogBeforeAds;
    }

    public Integer getDialogTimeInSec() {
        return dialogTimeInSec;
    }

    public void setDialogTimeInSec(Integer dialogTimeInSec) {
        this.dialogTimeInSec = dialogTimeInSec;
    }



    public Integer getAdsCloseCount() {
        return adsCloseCount;
    }

    public void setAdsCloseCount(Integer adsCloseCount) {
        this.adsCloseCount = adsCloseCount;
    }
}
