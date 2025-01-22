package com.water.alkaline.kengen.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.water.alkaline.kengen.model.main.Subcategory;

import java.util.List;

@Dao
public interface SubcatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Subcategory> entity);

    @Query("DELETE from tbl_subcategory")
    void deleteAll();

    @Query("SELECT * from tbl_subcategory")
    List<Subcategory> getAll();

    @Query("SELECT * from tbl_subcategory where catid = :data")
    List<Subcategory> getAllbyCategory(String data);

}
