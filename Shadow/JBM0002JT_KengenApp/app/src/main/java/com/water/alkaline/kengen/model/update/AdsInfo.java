
package com.water.alkaline.kengen.model.update;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdsInfo {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("GoogleBannerAds")
    @Expose
    private String GoogleBannerAds;
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
    @SerializedName("GoogleSplashOpenAdsOnOff")
    @Expose
    private Boolean googleSplashOpenAdsOnOff;
    @SerializedName("GoogleExitSplashInterOnOff")
    @Expose
    private Boolean googleExitSplashInterOnOff;
    @SerializedName("GoogleAppOpenAdsOnOff")
    @Expose
    private Boolean googleAppOpenAdsOnOff;
    @SerializedName("GoogleBannerOnOff")
    @Expose
    private Boolean GoogleBannerOnOff;
    @SerializedName("BannerAdWhichOne")
    @Expose
    private Integer BannerAdWhichOne;
    @SerializedName("AppOpen")
    @Expose
    private Integer AppOpen;
    @SerializedName("IntervalCount")
    @Expose
    private Integer IntervalCount;
    @SerializedName("BackIntervalCount")
    @Expose
    private Integer BackIntervalCount;
    @SerializedName("GoogleInterOnOff")
    @Expose
    private Boolean GoogleInterOnOff;
    @SerializedName("GoogleBackInterOnOff")
    @Expose
    private Boolean GoogleBackInterOnOff;
    @SerializedName("GoogleMiniNativeOnOff")
    @Expose
    private Boolean googleMiniNativeOnOff;
    @SerializedName("GoogleLargeNativeOnOff")
    @Expose
    private Boolean googleLargeNativeOnOff;
    @SerializedName("GoogleListNativeOnOff")
    @Expose
    private Boolean googleListNativeOnOff;
    @SerializedName("ListNativeWhichOne")
    @Expose
    private Integer listNativeWhichOne;
    @SerializedName("ListNativeAfterCount")
    @Expose
    private Integer listNativeAfterCount;
    @SerializedName("QurekaIconOnOff")
    @Expose
    private Boolean QurekaIconOnOff;
    @SerializedName("QurekaBannerOnOff")
    @Expose
    private Boolean QurekaBannerOnOff;
    @SerializedName("QurekaInterOnOff")
    @Expose
    private Boolean QurekaInterOnOff;
    @SerializedName("QurekaBackInterOnOff")
    @Expose
    private Boolean QurekaBackInterOnOff;
    @SerializedName("QurekaMiniNativeOnOff")
    @Expose
    private Boolean qurekaMiniNativeOnOff;
    @SerializedName("QurekaLargeNativeOnOff")
    @Expose
    private Boolean qurekaLargeNativeOnOff;
    @SerializedName("QurekaListNativeOnOff")
    @Expose
    private Boolean qurekaListNativeOnOff;
    @SerializedName("QurekaAppOpenOnOff")
    @Expose
    private Boolean qurekaAppOpenOnOff;
    @SerializedName("ShowDialogBeforeAds")
    @Expose
    private Boolean showDialogBeforeAds;
    @SerializedName("DialogTimeInSec")
    @Expose
    private Integer dialogTimeInSec;
    @SerializedName("VpnOnOff")
    @Expose
    private Boolean vpnOnOff;
    @SerializedName("VpnUrl")
    @Expose
    private String vpnUrl;
    @SerializedName("VpnAuto")
    @Expose
    private Boolean VpnAuto;
    @SerializedName("adsCloseCount")
    @Expose
    private Integer adsCloseCount;

    @SerializedName("isList")
    @Expose
    private Boolean isList;

    public Boolean getList() {
        return isList;
    }

    public void setList(Boolean list) {
        isList = list;
    }

    public Integer getAppOpen() {
        return AppOpen;
    }

    public void setAppOpen(Integer appOpen) {
        AppOpen = appOpen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean getVpnAuto() {
        return VpnAuto;
    }

    public void setVpnAuto(Boolean vpnAuto) {
        VpnAuto = vpnAuto;
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

    public Boolean getGoogleSplashOpenAdsOnOff() {
        return googleSplashOpenAdsOnOff;
    }

    public void setGoogleSplashOpenAdsOnOff(Boolean googleSplashOpenAdsOnOff) {
        this.googleSplashOpenAdsOnOff = googleSplashOpenAdsOnOff;
    }

    public Boolean getGoogleExitSplashInterOnOff() {
        return googleExitSplashInterOnOff;
    }

    public void setGoogleExitSplashInterOnOff(Boolean googleExitSplashInterOnOff) {
        this.googleExitSplashInterOnOff = googleExitSplashInterOnOff;
    }

    public Boolean getGoogleAppOpenAdsOnOff() {
        return googleAppOpenAdsOnOff;
    }

    public void setGoogleAppOpenAdsOnOff(Boolean googleAppOpenAdsOnOff) {
        this.googleAppOpenAdsOnOff = googleAppOpenAdsOnOff;
    }


    public Boolean getGoogleMiniNativeOnOff() {
        return googleMiniNativeOnOff;
    }

    public void setGoogleMiniNativeOnOff(Boolean googleMiniNativeOnOff) {
        this.googleMiniNativeOnOff = googleMiniNativeOnOff;
    }

    public Boolean getGoogleLargeNativeOnOff() {
        return googleLargeNativeOnOff;
    }

    public void setGoogleLargeNativeOnOff(Boolean googleLargeNativeOnOff) {
        this.googleLargeNativeOnOff = googleLargeNativeOnOff;
    }

    public Boolean getGoogleListNativeOnOff() {
        return googleListNativeOnOff;
    }

    public void setGoogleListNativeOnOff(Boolean googleListNativeOnOff) {
        this.googleListNativeOnOff = googleListNativeOnOff;
    }

    public Integer getListNativeWhichOne() {
        return listNativeWhichOne;
    }

    public void setListNativeWhichOne(Integer listNativeWhichOne) {
        this.listNativeWhichOne = listNativeWhichOne;
    }

    public Integer getListNativeAfterCount() {
        return listNativeAfterCount;
    }

    public void setListNativeAfterCount(Integer listNativeAfterCount) {
        this.listNativeAfterCount = listNativeAfterCount;
    }


    public Boolean getQurekaIconOnOff() {
        return QurekaIconOnOff;
    }

    public void setQurekaIconOnOff(Boolean qurekaIconOnOff) {
        QurekaIconOnOff = qurekaIconOnOff;
    }

    public Boolean getQurekaMiniNativeOnOff() {
        return qurekaMiniNativeOnOff;
    }

    public void setQurekaMiniNativeOnOff(Boolean qurekaMiniNativeOnOff) {
        this.qurekaMiniNativeOnOff = qurekaMiniNativeOnOff;
    }

    public Boolean getQurekaLargeNativeOnOff() {
        return qurekaLargeNativeOnOff;
    }

    public void setQurekaLargeNativeOnOff(Boolean qurekaLargeNativeOnOff) {
        this.qurekaLargeNativeOnOff = qurekaLargeNativeOnOff;
    }

    public Boolean getQurekaListNativeOnOff() {
        return qurekaListNativeOnOff;
    }

    public void setQurekaListNativeOnOff(Boolean qurekaListNativeOnOff) {
        this.qurekaListNativeOnOff = qurekaListNativeOnOff;
    }

    public Boolean getQurekaAppOpenOnOff() {
        return qurekaAppOpenOnOff;
    }

    public void setQurekaAppOpenOnOff(Boolean qurekaAppOpenOnOff) {
        this.qurekaAppOpenOnOff = qurekaAppOpenOnOff;
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

    public Boolean getVpnOnOff() {
        return vpnOnOff;
    }

    public void setVpnOnOff(Boolean vpnOnOff) {
        this.vpnOnOff = vpnOnOff;
    }

    public String getVpnUrl() {
        return vpnUrl;
    }

    public void setVpnUrl(String vpnUrl) {
        this.vpnUrl = vpnUrl;
    }

    public Integer getAdsCloseCount() {
        return adsCloseCount;
    }

    public void setAdsCloseCount(Integer adsCloseCount) {
        this.adsCloseCount = adsCloseCount;
    }

    public Integer getIntervalCount() {
        return IntervalCount;
    }

    public void setIntervalCount(Integer intervalCount) {
        IntervalCount = intervalCount;
    }

    public Integer getBackIntervalCount() {
        return BackIntervalCount;
    }

    public void setBackIntervalCount(Integer backIntervalCount) {
        BackIntervalCount = backIntervalCount;
    }

    public Boolean getGoogleInterOnOff() {
        return GoogleInterOnOff;
    }

    public void setGoogleInterOnOff(Boolean googleInterOnOff) {
        GoogleInterOnOff = googleInterOnOff;
    }

    public Boolean getGoogleBackInterOnOff() {
        return GoogleBackInterOnOff;
    }

    public void setGoogleBackInterOnOff(Boolean googleBackInterOnOff) {
        GoogleBackInterOnOff = googleBackInterOnOff;
    }

    public Boolean getQurekaInterOnOff() {
        return QurekaInterOnOff;
    }

    public void setQurekaInterOnOff(Boolean qurekaInterOnOff) {
        QurekaInterOnOff = qurekaInterOnOff;
    }

    public Boolean getQurekaBackInterOnOff() {
        return QurekaBackInterOnOff;
    }

    public void setQurekaBackInterOnOff(Boolean qurekaBackInterOnOff) {
        QurekaBackInterOnOff = qurekaBackInterOnOff;
    }

    public String getGoogleBannerAds() {
        return GoogleBannerAds;
    }

    public void setGoogleBannerAds(String googleBannerAds) {
        GoogleBannerAds = googleBannerAds;
    }

    public Boolean getGoogleBannerOnOff() {
        return GoogleBannerOnOff;
    }

    public void setGoogleBannerOnOff(Boolean googleBannerOnOff) {
        GoogleBannerOnOff = googleBannerOnOff;
    }

    public Integer getBannerAdWhichOne() {
        return BannerAdWhichOne;
    }

    public void setBannerAdWhichOne(Integer bannerAdWhichOne) {
        BannerAdWhichOne = bannerAdWhichOne;
    }

    public Boolean getQurekaBannerOnOff() {
        return QurekaBannerOnOff;
    }

    public void setQurekaBannerOnOff(Boolean qurekaBannerOnOff) {
        QurekaBannerOnOff = qurekaBannerOnOff;
    }
}
