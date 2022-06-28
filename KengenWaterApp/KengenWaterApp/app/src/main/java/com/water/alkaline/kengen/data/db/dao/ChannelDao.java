package com.water.alkaline.kengen.data.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.water.alkaline.kengen.model.main.Channel;

import java.util.List;

@Dao
public interface ChannelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Channel> entity);

    @Query("DELETE from tbl_channel")
    void deleteAll();

    @Query("SELECT * from tbl_channel")
    List<Channel> getAll();

    @Query("SELECT * from tbl_channel where catid = :data")
    List<Channel> getAllbyCategory(String data);
}
