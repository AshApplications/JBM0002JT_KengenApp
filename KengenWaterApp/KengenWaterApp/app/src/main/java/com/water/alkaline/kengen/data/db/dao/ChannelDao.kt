package com.water.alkaline.kengen.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.water.alkaline.kengen.model.main.Channel


@Dao
interface ChannelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: List<Channel>)

    @Query("DELETE from tbl_channel")
    fun deleteAll()

    @get:Query("SELECT * from tbl_channel")
    val all: MutableList<Channel>

    @Query("SELECT * from tbl_channel where catid = :data")
    fun getAllbyCategory(data: String): MutableList<Channel>
}
