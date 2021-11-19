package com.water.alkaline.kengen.data.db.repo;

import android.app.Application;

import com.water.alkaline.kengen.data.db.AppDB;
import com.water.alkaline.kengen.data.db.dao.DownloadDao;
import com.water.alkaline.kengen.model.DownloadEntity;

import java.util.List;

public class DownloadRepo {
    DownloadDao dao;

    public DownloadRepo(Application application) {
        AppDB database = AppDB.getInstance(application);
        dao = database.downloadDao();
    }

    public void insert(DownloadEntity entity) {
        dao.insert(entity);
    }

    public void deleteAll() {
        dao.deleteAll();
    }

    public List<DownloadEntity> getAll() {
        return dao.getAll();
    }

    public List<DownloadEntity> getAllbyID(int data) {
        return dao.getAllbyID(data);
    }

    public List<DownloadEntity> getbyUrl(String data) {
        return dao.getbyUrl(data);
    }

    public List<DownloadEntity> getbyPath(String data) {
        return dao.getbyPath(data);
    }
}
