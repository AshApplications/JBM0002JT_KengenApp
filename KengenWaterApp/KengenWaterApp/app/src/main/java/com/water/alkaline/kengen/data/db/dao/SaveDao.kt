package com.water.alkaline.kengen.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.water.alkaline.kengen.model.SaveEntity

@Dao
interface SaveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: SaveEntity)

    @Delete
    fun delete(entity: SaveEntity)

    @Query("DELETE from tbl_likes")
    fun deleteAll()

    @get:Query("SELECT * from tbl_likes")
    val all: MutableList<SaveEntity>

    @Query("SELECT * from tbl_likes where videoId = :data")
    fun getbyVideoId(data: String): MutableList<SaveEntity>
}
