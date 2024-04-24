
package com.water.alkaline.kengen.model.update;

import java.util.List;

import com.google.gms.ads.AdModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("app_info")
    @Expose
    private List<AppInfo> appInfo = null;
    @SerializedName("ads_info")
    @Expose
    private List<AdModel> adsInfo = null;

    public List<AppInfo> getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(List<AppInfo> appInfo) {
        this.appInfo = appInfo;
    }

    public List<AdModel> getAdsInfo() {
        return adsInfo;
    }

    public void setAdsInfo(List<AdModel> adsInfo) {
        this.adsInfo = adsInfo;
    }

}
