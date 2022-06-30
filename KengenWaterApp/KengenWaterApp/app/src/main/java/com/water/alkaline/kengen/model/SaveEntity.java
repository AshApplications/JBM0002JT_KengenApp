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

    public SaveEntity(@NonNull String videoId, String title, String des, String imgUrl) {
        this.videoId = videoId;
        this.title = title;
        this.des = des;
        this.imgUrl = imgUrl;
    }
}
