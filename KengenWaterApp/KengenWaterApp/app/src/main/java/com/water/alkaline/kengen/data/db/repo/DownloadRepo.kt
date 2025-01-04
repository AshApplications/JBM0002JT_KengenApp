package com.water.alkaline.kengen.data.db.repo

import com.water.alkaline.kengen.data.db.dao.DownloadDao
import com.water.alkaline.kengen.model.DownloadEntity
import javax.inject.Inject

class DownloadRepo @Inject constructor(val dao: DownloadDao) {

    fun insert(entity: DownloadEntity?) {
        dao.insert(entity)
    }

    fun deleteAll() {
        dao.deleteAll()
    }

    val all: List<DownloadEntity>
        get() = dao.all

    fun getAllByID(data: Int): List<DownloadEntity> {
        return dao.getAllbyID(data)
    }

    fun getByUrl(data: String?): List<DownloadEntity> {
        return dao.getbyUrl(data)
    }

    fun getByPath(data: String?): List<DownloadEntity> {
        return dao.getbyPath(data)
    }
}
