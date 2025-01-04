package com.water.alkaline.kengen.data.db.repo

import com.water.alkaline.kengen.data.db.dao.SaveDao
import com.water.alkaline.kengen.model.SaveEntity
import javax.inject.Inject

class SaveRepo @Inject constructor(val dao: SaveDao){

    fun insert(entity: SaveEntity?) {
        dao.insert(entity)
    }

    fun delete(entity: SaveEntity?) {
        dao.delete(entity)
    }

    fun deleteAll() {
        dao.deleteAll()
    }

    val all: List<SaveEntity>
        get() = dao.all

    fun getByVideoId(data: String?): List<SaveEntity> {
        return dao.getbyVideoId(data)
    }
}
