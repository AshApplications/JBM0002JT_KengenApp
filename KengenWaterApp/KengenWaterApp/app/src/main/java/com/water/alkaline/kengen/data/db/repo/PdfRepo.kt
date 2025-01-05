package com.water.alkaline.kengen.data.db.repo

import com.water.alkaline.kengen.data.db.dao.PdfDao
import com.water.alkaline.kengen.model.main.Pdf
import javax.inject.Inject

class PdfRepo @Inject constructor(val dao: PdfDao){

    fun insert(entity: List<Pdf>) {
        dao.insert(entity)
    }

    fun deleteAll() {
        dao.deleteAll()
    }

    val all: MutableList<Pdf>
        get() = dao.all

    fun getAllByCategory(data: String): MutableList<Pdf> {
        return dao.getAllbyCategory(data)
    }

    fun getByUrl(data: String): MutableList<Pdf> {
        return dao.getbyUrl(data)
    }
}
