package com.water.alkaline.kengen.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.water.alkaline.kengen.model.main.Pdf;

import java.util.List;

@Dao
public interface PdfDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Pdf> entity);

    @Query("DELETE from tbl_pdf")
    void deleteAll();

    @Query("SELECT * from tbl_pdf")
    List<Pdf> getAll();

    @Query("SELECT * from tbl_pdf where catid = :data")
    List<Pdf> getAllbyCategory(String data);

    @Query("SELECT * from tbl_pdf where url = :data")
    List<Pdf> getbyUrl(String data);
}
