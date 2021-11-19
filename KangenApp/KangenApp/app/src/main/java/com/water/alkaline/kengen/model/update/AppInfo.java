package com.water.alkaline.kengen.model.update;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppInfo {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("flag")
    @Expose
    private String flag;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("appNotice")
    @Expose
    private String appNotice;

    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("buttonName")
    @Expose
    private String buttonName;
    @SerializedName("buttonSkip")
    @Expose
    private String buttonSkip;
    @SerializedName("todayDate")
    @Expose
    private String todayDate;
    @SerializedName("keyId")
    @Expose
    private String keyId;
    @SerializedName("api_key")
    @Expose
    private String apiKey;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getButtonSkip() {
        return buttonSkip;
    }

    public void setButtonSkip(String buttonSkip) {
        this.buttonSkip = buttonSkip;
    }

    public String getTodayDate() {
        return todayDate;
    }

    public void setTodayDate(String todayDate) {
        this.todayDate = todayDate;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAppNotice() {
        return appNotice;
    }

    public void setAppNotice(String appNotice) {
        this.appNotice = appNotice;
    }
}