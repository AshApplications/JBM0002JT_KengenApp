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

    public DownloadEntity() {
    }

    public DownloadEntity(String name, String filePath, String imgUrl, String url, int type) {
        this.name = name;
        this.filePath = filePath;
        this.imgUrl = imgUrl;
        this.url = url;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
