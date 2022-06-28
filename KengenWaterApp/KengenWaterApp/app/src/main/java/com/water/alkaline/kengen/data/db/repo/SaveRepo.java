package com.water.alkaline.kengen.data.db.repo;

import android.app.Application;

import com.water.alkaline.kengen.data.db.AppDB;
import com.water.alkaline.kengen.data.db.dao.SaveDao;
import com.water.alkaline.kengen.model.SaveEntity;

import java.util.List;

public class SaveRepo {
    SaveDao dao;

    public SaveRepo(Application application) {
        AppDB database = AppDB.getInstance(application);
        dao = database.saveDao();
    }

    public void insert(SaveEntity entity) {
        dao.insert(entity);
    }
    public void delete(SaveEntity entity) {
        dao.delete(entity);
    }
    public void deleteAll() {
        dao.deleteAll();
    }

    public List<SaveEntity> getAll() {
        return dao.getAll();
    }

    public List<SaveEntity> getbyVideoId(String data) {
        return dao.getbyVideoId(data);
    }
}
