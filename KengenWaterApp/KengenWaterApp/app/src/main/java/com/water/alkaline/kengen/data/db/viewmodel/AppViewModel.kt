package com.water.alkaline.kengen.data.db.viewmodel

import androidx.lifecycle.ViewModel
import com.water.alkaline.kengen.data.db.repo.BannerRepo
import com.water.alkaline.kengen.data.db.repo.CategoryRepo
import com.water.alkaline.kengen.data.db.repo.ChannelRepo
import com.water.alkaline.kengen.data.db.repo.DownloadRepo
import com.water.alkaline.kengen.data.db.repo.PdfRepo
import com.water.alkaline.kengen.data.db.repo.SaveRepo
import com.water.alkaline.kengen.data.db.repo.SubCategoryRepo
import com.water.alkaline.kengen.model.DownloadEntity
import com.water.alkaline.kengen.model.SaveEntity
import com.water.alkaline.kengen.model.main.Banner
import com.water.alkaline.kengen.model.main.Category
import com.water.alkaline.kengen.model.main.Channel
import com.water.alkaline.kengen.model.main.Pdf
import com.water.alkaline.kengen.model.main.Subcategory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val categoryRepo: CategoryRepo,
    private val subCategoryRepo: SubCategoryRepo,
    private val channelRepo: ChannelRepo,
    private val pdfRepo: PdfRepo,
    private val bannerRepo: BannerRepo,
    private val downloadRepo: DownloadRepo,
    private val saveRepo: SaveRepo
) : ViewModel() {

    fun insertCategory(entity: List<Category>) {
        categoryRepo.insert(entity)
    }

    fun deleteAllCategory() {
        categoryRepo.deleteAll()
    }

    val allCategory: List<Category>
        get() = categoryRepo.all


    fun insertSubCategory(entity: List<Subcategory>) {
        subCategoryRepo.insert(entity)
    }

    fun deleteAllSubCategory() {
        subCategoryRepo.deleteAll()
    }

    val allSubCategory: List<Subcategory>
        get() = subCategoryRepo.all

    fun getAllSubByCategory(data: String): List<Subcategory> {
        return subCategoryRepo.getAllByCategory(data)
    }


    fun insertChannel(entity: List<Channel>) {
        channelRepo.insert(entity)
    }

    fun deleteAllChannel() {
        channelRepo.deleteAll()
    }

    val allChannel: List<Channel>
        get() = channelRepo.all

    fun getAllChannelByCategory(data: String): List<Channel> {
        return channelRepo.getAllByCategory(data)
    }


    fun insertPdf(entity: List<Pdf>) {
        pdfRepo.insert(entity)
    }

    fun deleteAllPdf() {
        pdfRepo.deleteAll()
    }

    val allPdf: List<Pdf>
        get() = pdfRepo.all

    fun getAllPdfByCategory(data: String): List<Pdf> {
        return pdfRepo.getAllByCategory(data)
    }

    fun getByUrl(data: String): List<Pdf> {
        return pdfRepo.getByUrl(data)
    }

    fun insertBanner(entity: List<Banner>) {
        bannerRepo.insert(entity)
    }

    fun deleteAllBanner() {
        bannerRepo.deleteAll()
    }

    val allBanner: List<Banner>
        get() = bannerRepo.all

    fun getAllBannerByCategory(data: String): List<Banner> {
        return bannerRepo.getAllByCategory(data)
    }

    fun getBannerByUrl(data: String): List<Banner> {
        return bannerRepo.getByUrl(data)
    }


    fun insertDownloads(entity: DownloadEntity) {
        downloadRepo.insert(entity)
    }

    fun deleteAllDownloads() {
        downloadRepo.deleteAll()
    }

    val allDownloads: List<DownloadEntity>
        get() = downloadRepo.all

    fun getAllbyID(data: Int): List<DownloadEntity> {
        return downloadRepo.getAllByID(data)
    }

    fun getDownloadByUrl(data: String): List<DownloadEntity> {
        return downloadRepo.getByUrl(data)
    }

    fun getByPath(data: String): List<DownloadEntity> {
        return downloadRepo.getByPath(data)
    }

    fun insertSaves(entity: SaveEntity) {
        saveRepo.insert(entity)
    }

    fun deleteSaves(entity: SaveEntity) {
        saveRepo.delete(entity)
    }

    fun deleteAllSaves() {
        saveRepo.deleteAll()
    }

    val allSaves: List<SaveEntity>
        get() = saveRepo.all

    fun getSaveByVideoId(data: String): List<SaveEntity> {
        return saveRepo.getByVideoId(data)
    }
}
