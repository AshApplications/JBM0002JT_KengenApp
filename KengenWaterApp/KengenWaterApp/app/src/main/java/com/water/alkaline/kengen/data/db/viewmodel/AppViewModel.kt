package com.water.alkaline.kengen.data.db.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.water.alkaline.kengen.data.db.repo.BannerRepo;
import com.water.alkaline.kengen.data.db.repo.CategoryRepo;
import com.water.alkaline.kengen.data.db.repo.ChannelRepo;
import com.water.alkaline.kengen.data.db.repo.DownloadRepo;
import com.water.alkaline.kengen.data.db.repo.PdfRepo;
import com.water.alkaline.kengen.data.db.repo.SaveRepo;
import com.water.alkaline.kengen.data.db.repo.SubCategoryRepo;
import com.water.alkaline.kengen.model.DownloadEntity;
import com.water.alkaline.kengen.model.SaveEntity;
import com.water.alkaline.kengen.model.main.Banner;
import com.water.alkaline.kengen.model.main.Category;
import com.water.alkaline.kengen.model.main.Channel;
import com.water.alkaline.kengen.model.main.Pdf;
import com.water.alkaline.kengen.model.main.Subcategory;

import java.util.List;

public class AppViewModel extends AndroidViewModel {

    CategoryRepo categoryRepo;
    SubCategoryRepo subCategoryRepo;
    ChannelRepo channelRepo;
    PdfRepo pdfRepo;
    BannerRepo bannerRepo;
    DownloadRepo downloadRepo;
    SaveRepo saveRepo;

    public AppViewModel(@NonNull Application application) {
        super(application);
        categoryRepo = new CategoryRepo(application);
        subCategoryRepo = new SubCategoryRepo(application);
        channelRepo = new ChannelRepo(application);
        pdfRepo = new PdfRepo(application);
        bannerRepo = new BannerRepo(application);
        downloadRepo = new DownloadRepo(application);
        saveRepo = new SaveRepo(application);

    }

    public void insertCategory(List<Category> entity) {
        categoryRepo.insert(entity);
    }

    public void deleteAllCategory() {
        categoryRepo.deleteAll();
    }

    public List<Category> getAllCategory() {
        return categoryRepo.getAll();
    }


    public void insertSubCategory(List<Subcategory> entity) {
        subCategoryRepo.insert(entity);
    }

    public void deleteAllSubCategory() {
        subCategoryRepo.deleteAll();
    }

    public List<Subcategory> getAllSubCategory() {
        return subCategoryRepo.getAll();
    }

    public List<Subcategory> getAllSubByCategory(String data) {
        return subCategoryRepo.getAllbyCategory(data);
    }


    public void insertChannel(List<Channel> entity) {
        channelRepo.insert(entity);
    }

    public void deleteAllChannel() {
        channelRepo.deleteAll();
    }

    public List<Channel> getAllChannel() {
        return channelRepo.getAll();
    }

    public List<Channel> getAllChannelByCategory(String data) {
        return channelRepo.getAllbyCategory(data);
    }


    public void insertPdf(List<Pdf> entity) {
        pdfRepo.insert(entity);
    }

    public void deleteAllPdf() {
        pdfRepo.deleteAll();
    }

    public List<Pdf> getAllPdf() {
        return pdfRepo.getAll();
    }

    public List<Pdf> getAllPdfByCategory(String data) {
        return pdfRepo.getAllbyCategory(data);
    }

    public List<Pdf> getbyUrl(String data) {
        return pdfRepo.getbyUrl(data);
    }

    public void insertBanner(List<Banner> entity) {
        bannerRepo.insert(entity);
    }

    public void deleteAllBanner() {
        bannerRepo.deleteAll();
    }

    public List<Banner> getAllBanner() {
        return bannerRepo.getAll();
    }

    public List<Banner> getAllBannerByCategory(String data) {
        return bannerRepo.getAllbyCategory(data);
    }

    public List<Banner> getBannerbyUrl(String data) {
        return bannerRepo.getbyUrl(data);
    }


    public void insertDownloads(DownloadEntity entity) {
        downloadRepo.insert(entity);
    }

    public void deleteAllDownloads() {
        downloadRepo.deleteAll();
    }

    public List<DownloadEntity> getAllDownloads() {
        return downloadRepo.getAll();
    }

    public List<DownloadEntity> getAllbyID(int data) {
        return downloadRepo.getAllbyID(data);
    }

    public List<DownloadEntity> getDownloadbyUrl(String data) {
        return downloadRepo.getbyUrl(data);
    }

    public List<DownloadEntity> getbyPath(String data) {
        return downloadRepo.getbyPath(data);
    }

    public void insertSaves(SaveEntity entity) {
        saveRepo.insert(entity);
    }
    public void deleteSaves(SaveEntity entity) {
        saveRepo.delete(entity);
    }

    public void deleteAllSaves() {
        saveRepo.deleteAll();
    }

    public List<SaveEntity> getAllSaves() {
        return saveRepo.getAll();
    }

    public List<SaveEntity> getSavebyVideoId(String data) {
        return saveRepo.getbyVideoId(data);
    }

}
