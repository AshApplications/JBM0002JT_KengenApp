package com.water.alkaline.kengen.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.water.alkaline.kengen.model.DownloadEntity

@Dao
interface DownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: DownloadEntity)

    @Query("DELETE from tbl_downloads")
    fun deleteAll()

    @get:Query("SELECT * from tbl_downloads")
    val all: MutableList<DownloadEntity>

    @Query("SELECT * from tbl_downloads where id = :data")
    fun getAllbyID(data: Int): MutableList<DownloadEntity>

    @Query("SELECT * from tbl_downloads where url = :data")
    fun getbyUrl(data: String): MutableList<DownloadEntity>

    @Query("SELECT * from tbl_downloads where filePath = :data")
    fun getbyPath(data: String): MutableList<DownloadEntity>
}

