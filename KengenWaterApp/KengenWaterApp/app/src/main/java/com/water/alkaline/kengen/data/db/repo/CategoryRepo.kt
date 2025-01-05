package com.water.alkaline.kengen.data.db.repo

import com.water.alkaline.kengen.data.db.dao.CategoryDao
import com.water.alkaline.kengen.model.main.Category
import javax.inject.Inject

class CategoryRepo @Inject constructor(val dao: CategoryDao) {

    fun insert(entity: List<Category>) {
        dao.insert(entity)
    }

    fun deleteAll() {
        dao.deleteAll()
    }

    val all: MutableList<Category>
        get() = dao.all
}
