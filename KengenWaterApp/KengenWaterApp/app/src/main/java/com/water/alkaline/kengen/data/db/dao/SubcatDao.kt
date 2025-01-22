package com.water.alkaline.kengen.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.water.alkaline.kengen.model.main.Subcategory

@Dao
interface SubcatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: List<Subcategory>)

    @Query("DELETE from tbl_subcategory")
    fun deleteAll()

    @get:Query("SELECT * from tbl_subcategory")
    val all: MutableList<Subcategory>

    @Query("SELECT * from tbl_subcategory where catid = :data")
    fun getAllbyCategory(data: String): MutableList<Subcategory>
}
