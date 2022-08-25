package com.water.alkaline.kengen.data.db.repo;

import android.app.Application;

import com.water.alkaline.kengen.data.db.AppDB;
import com.water.alkaline.kengen.data.db.dao.ChannelDao;
import com.water.alkaline.kengen.model.main.Channel;

import java.util.List;

public class ChannelRepo {
    ChannelDao dao;

    public ChannelRepo(Application application) {
        AppDB database = AppDB.getInstance(application);
        dao = database.channelDao();
    }

    public void insert(List<Channel> entity) {
        dao.insert(entity);
    }

    public void deleteAll() {
        dao.deleteAll();
    }

    public List<Channel> getAll() {
        return dao.getAll();
    }

    public List<Channel> getAllbyCategory(String data) {
        return dao.getAllbyCategory(data);
    }
}
