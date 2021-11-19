package com.water.alkaline.kengen.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "tbl_likes")
public class SaveEntity {

    @PrimaryKey
    @NonNull
    public String videoId;
    public String title;
    public String imgUrl;

    public SaveEntity(@NotNull String videoId, String title, String imgUrl) {
        this.videoId = videoId;
        this.title = title;
        this.imgUrl = imgUrl;
    }
}
