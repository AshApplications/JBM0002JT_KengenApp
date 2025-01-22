package com.water.alkaline.kengen.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.water.alkaline.kengen.model.main.Banner

@Dao
interface BannerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: List<Banner>)

    @Query("DELETE from tbl_banner")
    fun deleteAll()

    @get:Query("SELECT * from tbl_banner")
    val all: MutableList<Banner>

    @Query("SELECT * from tbl_banner where catid = :data")
    fun getAllbyCategory(data: String): MutableList<Banner>

    @Query("SELECT * from tbl_banner where url = :data")
    fun getbyUrl(data: String): MutableList<Banner>
}
