package com.water.alkaline.kengen.data.db.repo

import com.water.alkaline.kengen.data.db.dao.SubcatDao
import com.water.alkaline.kengen.model.main.Subcategory
import javax.inject.Inject

class SubCategoryRepo @Inject constructor(val dao: SubcatDao){

    fun insert(entity: List<Subcategory>) {
        dao.insert(entity)
    }

    fun deleteAll() {
        dao.deleteAll()
    }

    val all: MutableList<Subcategory>
        get() = dao.all

    fun getAllByCategory(data: String): MutableList<Subcategory> {
        return dao.getAllbyCategory(data)
    }
}
