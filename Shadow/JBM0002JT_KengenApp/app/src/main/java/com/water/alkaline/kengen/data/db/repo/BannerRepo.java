package com.water.alkaline.kengen.data.db.repo;

import android.app.Application;

import com.water.alkaline.kengen.data.db.AppDB;
import com.water.alkaline.kengen.data.db.dao.BannerDao;
import com.water.alkaline.kengen.model.main.Banner;

import java.util.List;

public class BannerRepo {
    BannerDao dao;

    public BannerRepo(Application application) {
        AppDB database = AppDB.getInstance(application);
        dao = database.bannerDao();
    }

    public void insert(List<Banner> entity) {
        dao.insert(entity);
    }

    public void deleteAll() {
        dao.deleteAll();
    }

    public List<Banner> getAll() {
        return dao.getAll();
    }

    public List<Banner> getAllbyCategory(String data) {
        return dao.getAllbyCategory(data);
    }


    public List<Banner> getbyUrl(String data) {
        return dao.getbyUrl(data);
    }
}
