package com.water.alkaline.kengen.data.db.repo;

import android.app.Application;

import com.water.alkaline.kengen.data.db.AppDB;
import com.water.alkaline.kengen.data.db.dao.CategoryDao;
import com.water.alkaline.kengen.model.main.Category;

import java.util.List;

public class CategoryRepo {
    CategoryDao dao;

    public CategoryRepo(Application application) {
        AppDB database = AppDB.getInstance(application);
        dao = database.categoryDao();
    }

    public void insert(List<Category> entity) {
        dao.insert(entity);
    }

    public void deleteAll() {
        dao.deleteAll();
    }

    public List<Category> getAll() {
        return dao.getAll();
    }

}
