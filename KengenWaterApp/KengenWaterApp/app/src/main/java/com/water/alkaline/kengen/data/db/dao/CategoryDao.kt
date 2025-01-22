package com.water.alkaline.kengen.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.water.alkaline.kengen.model.main.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Category> entity);

    @Query("DELETE from tbl_category")
    void deleteAll();

    @Query("SELECT * from tbl_category")
    List<Category> getAll();
}
