package com.water.alkaline.kengen.data.db.repo

import com.water.alkaline.kengen.data.db.dao.ChannelDao
import com.water.alkaline.kengen.model.main.Channel
import javax.inject.Inject

class ChannelRepo @Inject constructor(val dao: ChannelDao) {

    fun insert(entity: List<Channel?>?) {
        dao.insert(entity)
    }

    fun deleteAll() {
        dao.deleteAll()
    }

    val all: List<Channel>
        get() = dao.all

    fun getAllByCategory(data: String?): List<Channel> {
        return dao.getAllbyCategory(data)
    }
}
