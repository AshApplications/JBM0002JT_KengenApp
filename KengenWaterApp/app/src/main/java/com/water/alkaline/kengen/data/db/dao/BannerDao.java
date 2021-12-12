package com.water.alkaline.kengen.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.water.alkaline.kengen.model.main.Banner;

import java.util.List;

@Dao
public interface BannerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Banner> entity);

    @Query("DELETE from tbl_banner")
    void deleteAll();

    @Query("SELECT * from tbl_banner")
    List<Banner> getAll();

    @Query("SELECT * from tbl_banner where catid = :data")
    List<Banner> getAllbyCategory(String data);


    @Query("SELECT * from tbl_banner where url = :data")
    List<Banner> getbyUrl(String data);
}
