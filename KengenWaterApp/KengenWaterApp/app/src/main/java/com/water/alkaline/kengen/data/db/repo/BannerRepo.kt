package com.water.alkaline.kengen.data.db.repo

import com.water.alkaline.kengen.data.db.dao.BannerDao
import com.water.alkaline.kengen.model.main.Banner
import javax.inject.Inject

class BannerRepo @Inject constructor(val dao: BannerDao) {

    fun insert(entity: MutableList<Banner>) {
        dao.insert(entity)
    }

    fun deleteAll() {
        dao.deleteAll()
    }

    val all: MutableList<Banner>
        get() = dao.all

    fun getAllByCategory(data: String): MutableList<Banner> {
        return dao.getAllbyCategory(data)
    }

    fun getByUrl(data: String): MutableList<Banner> {
        return dao.getbyUrl(data)
    }
}
