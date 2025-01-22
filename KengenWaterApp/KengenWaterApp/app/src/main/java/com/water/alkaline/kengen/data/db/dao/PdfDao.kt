package com.water.alkaline.kengen.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.water.alkaline.kengen.model.main.Pdf

@Dao
interface PdfDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: List<Pdf>)

    @Query("DELETE from tbl_pdf")
    fun deleteAll()

    @get:Query("SELECT * from tbl_pdf")
    val all: MutableList<Pdf>

    @Query("SELECT * from tbl_pdf where catid = :data")
    fun getAllbyCategory(data: String): MutableList<Pdf>

    @Query("SELECT * from tbl_pdf where url = :data")
    fun getbyUrl(data: String): MutableList<Pdf>
}
