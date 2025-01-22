package com.water.alkaline.kengen.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "tbl_likes")
public class SaveEntity {

    @PrimaryKey
    @NonNull
    public String videoId;
    public String title;
    public String des;
    public String imgUrl;

    public SaveEntity() {
    }

    public SaveEntity(@NonNull String videoId, String title, String des, String imgUrl) {
        this.videoId = videoId;
        this.title = title;
        this.des = des;
        this.imgUrl = imgUrl;
    }

    @NonNull
    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(@NonNull String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
