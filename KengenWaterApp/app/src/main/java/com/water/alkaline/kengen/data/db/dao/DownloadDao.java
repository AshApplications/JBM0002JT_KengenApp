package com.water.alkaline.kengen.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.water.alkaline.kengen.model.DownloadEntity;

import java.util.List;

@Dao
public interface DownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DownloadEntity entity);

    @Query("DELETE from tbl_downloads")
    void deleteAll();

    @Query("SELECT * from tbl_downloads")
    List<DownloadEntity> getAll();

    @Query("SELECT * from tbl_downloads where id = :data")
    List<DownloadEntity> getAllbyID(int data);

    @Query("SELECT * from tbl_downloads where url = :data")
    List<DownloadEntity> getbyUrl(String data);

    @Query("SELECT * from tbl_downloads where filePath = :data")
    List<DownloadEntity> getbyPath(String data);
}

