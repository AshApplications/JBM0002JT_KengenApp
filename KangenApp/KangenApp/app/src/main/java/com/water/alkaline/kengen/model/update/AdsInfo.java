package com.water.alkaline.kengen.model.update;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdsInfo {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("ad_priority")
    @Expose
    public String adPriority;
    @SerializedName("gbanner")
    @Expose
    public String gbanner;
    @SerializedName("ginter")
    @Expose
    public String ginter;
    @SerializedName("gnative")
    @Expose
    public String gnative;
    @SerializedName("greward")
    @Expose
    public String greward;
    @SerializedName("gopen")
    @Expose
    public String gopen;
    @SerializedName("odealAppId")
    @Expose
    public String odealAppId;
    @SerializedName("qurekaOn")
    @Expose
    public String qurekaOn;

    @SerializedName("nativeOn")
    @Expose
    public String nativeOn;
    @SerializedName("nativeCount")
    @Expose
    public String nativeCount;

    @SerializedName("intervalCount")
    @Expose
    public String intervalCount;
    @SerializedName("clickCount")
    @Expose
    public String clickCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdPriority() {
        return adPriority;
    }

    public void setAdPriority(String adPriority) {
        this.adPriority = adPriority;
    }

    public String getGbanner() {
        return gbanner;
    }

    public void setGbanner(String gbanner) {
        this.gbanner = gbanner;
    }

    public String getGinter() {
        return ginter;
    }

    public void setGinter(String ginter) {
        this.ginter = ginter;
    }

    public String getGnative() {
        return gnative;
    }

    public void setGnative(String gnative) {
        this.gnative = gnative;
    }

    public String getGreward() {
        return greward;
    }

    public void setGreward(String greward) {
        this.greward = greward;
    }

    public String getGopen() {
        return gopen;
    }

    public void setGopen(String gopen) {
        this.gopen = gopen;
    }

    public String getOdealAppId() {
        return odealAppId;
    }

    public void setOdealAppId(String odealAppId) {
        this.odealAppId = odealAppId;
    }

    public String getQurekaOn() {
        return qurekaOn;
    }

    public void setQurekaOn(String qurekaOn) {
        this.qurekaOn = qurekaOn;
    }

    public String getIntervalCount() {
        return intervalCount;
    }

    public void setIntervalCount(String intervalCount) {
        this.intervalCount = intervalCount;
    }

    public String getClickCount() {
        return clickCount;
    }

    public void setClickCount(String clickCount) {
        this.clickCount = clickCount;
    }

    public String getNativeOn() {
        return nativeOn;
    }

    public void setNativeOn(String nativeOn) {
        this.nativeOn = nativeOn;
    }

    public String getNativeCount() {
        return nativeCount;
    }

    public void setNativeCount(String nativeCount) {
        this.nativeCount = nativeCount;
    }
}