package com.water.alkaline.kengen.model;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_downloads")
public class DownloadEntity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;

    public String name;
    public String filePath;
    public String imgUrl;
    public String url;

    public int type;

    public DownloadEntity(String name, String filePath, String imgUrl, String url, int type) {
        this.name = name;
        this.filePath = filePath;
        this.imgUrl = imgUrl;
        this.url = url;
        this.type = type;
    }
}
