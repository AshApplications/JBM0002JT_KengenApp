package com.water.alkaline.kengen.data.db.repo;

import android.app.Application;

import com.water.alkaline.kengen.data.db.AppDB;
import com.water.alkaline.kengen.data.db.dao.PdfDao;
import com.water.alkaline.kengen.model.main.Pdf;

import java.util.List;

public class PdfRepo {
    PdfDao dao;

    public PdfRepo(Application application) {
        AppDB database = AppDB.getInstance(application);
        dao = database.pdfDao();
    }

    public void insert(List<Pdf> entity) {
        dao.insert(entity);
    }

    public void deleteAll() {
        dao.deleteAll();
    }

    public List<Pdf> getAll() {
        return dao.getAll();
    }

    public List<Pdf> getAllbyCategory(String data) {
        return dao.getAllbyCategory(data);
    }

    public List<Pdf> getbyUrl(String data) {
        return dao.getbyUrl(data);
    }
}
