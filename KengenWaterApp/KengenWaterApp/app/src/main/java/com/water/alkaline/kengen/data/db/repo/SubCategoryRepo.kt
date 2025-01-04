package com.water.alkaline.kengen.data.db.repo;

import android.app.Application;

import com.water.alkaline.kengen.data.db.AppDB;
import com.water.alkaline.kengen.data.db.dao.SubcatDao;
import com.water.alkaline.kengen.model.main.Subcategory;

import java.util.List;

public class SubCategoryRepo {
    SubcatDao dao;

    public SubCategoryRepo(Application application) {
        AppDB database = AppDB.getInstance(application);
        dao = database.subcatDao();
    }

    public void insert(List<Subcategory> entity) {
        dao.insert(entity);
    }

    public void deleteAll() {
        dao.deleteAll();
    }

    public List<Subcategory> getAll() {
        return dao.getAll();
    }

    public List<Subcategory> getAllbyCategory(String data) {
        return dao.getAllbyCategory(data);
    }

}
