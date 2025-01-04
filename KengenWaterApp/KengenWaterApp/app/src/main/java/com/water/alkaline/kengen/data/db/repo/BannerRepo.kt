package com.water.alkaline.kengen.data.db.repo

import com.water.alkaline.kengen.data.db.dao.BannerDao
import com.water.alkaline.kengen.model.main.Banner
import javax.inject.Inject

class BannerRepo @Inject constructor(val dao: BannerDao) {

    fun insert(entity: List<Banner?>?) {
        dao.insert(entity)
    }

    fun deleteAll() {
        dao.deleteAll()
    }

    val all: List<Banner>
        get() = dao.all

    fun getAllByCategory(data: String?): List<Banner> {
        return dao.getAllbyCategory(data)
    }

    fun getByUrl(data: String?): List<Banner> {
        return dao.getbyUrl(data)
    }
}
