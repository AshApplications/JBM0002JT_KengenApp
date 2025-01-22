package com.water.alkaline.kengen.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.water.alkaline.kengen.model.main.Category

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: List<Category>)

    @Query("DELETE from tbl_category")
    fun deleteAll()

    @get:Query("SELECT * from tbl_category")
    val all: MutableList<Category>
}
