package com.water.alkaline.kengen.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.water.alkaline.kengen.model.SaveEntity;

import java.util.List;

@Dao
public interface SaveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SaveEntity entity);

    @Delete
    void delete(SaveEntity entity);

    @Query("DELETE from tbl_likes")
    void deleteAll();


    @Query("SELECT * from tbl_likes")
    List<SaveEntity> getAll();

    @Query("SELECT * from tbl_likes where videoId = :data")
    List<SaveEntity> getbyVideoId(String data);
}
