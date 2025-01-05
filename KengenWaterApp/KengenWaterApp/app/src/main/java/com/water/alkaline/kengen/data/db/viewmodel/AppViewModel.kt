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

    fun insertCategory(entity: MutableList<Category>) {
        categoryRepo.insert(entity)
    }

    fun deleteAllCategory() {
        categoryRepo.deleteAll()
    }

    val allCategory: MutableList<Category>
        get() = categoryRepo.all

    fun insertSubCategory(entity: MutableList<Subcategory>) {
        subCategoryRepo.insert(entity)
    }

    fun deleteAllSubCategory() {
        subCategoryRepo.deleteAll()
    }

    fun getAllSubByCategory(data: String): MutableList<Subcategory> {
        return subCategoryRepo.getAllByCategory(data)
    }

    fun insertChannel(entity: MutableList<Channel>) {
        channelRepo.insert(entity)
    }

    fun deleteAllChannel() {
        channelRepo.deleteAll()
    }

    fun getAllChannelByCategory(data: String): MutableList<Channel> {
        return channelRepo.getAllByCategory(data)
    }

    fun insertPdf(entity: MutableList<Pdf>) {
        pdfRepo.insert(entity)
    }

    fun deleteAllPdf() {
        pdfRepo.deleteAll()
    }

    fun getAllPdfByCategory(data: String): MutableList<Pdf> {
        return pdfRepo.getAllByCategory(data)
    }

    fun getByUrl(data: String): MutableList<Pdf> {
        return pdfRepo.getByUrl(data)
    }

    fun insertBanner(entity: MutableList<Banner>) {
        bannerRepo.insert(entity)
    }

    fun deleteAllBanner() {
        bannerRepo.deleteAll()
    }

    fun getAllBannerByCategory(data: String): MutableList<Banner> {
        return bannerRepo.getAllByCategory(data)
    }

    fun getBannerByUrl(data: String): MutableList<Banner> {
        return bannerRepo.getByUrl(data)
    }

    fun insertDownloads(entity: DownloadEntity) {
        downloadRepo.insert(entity)
    }

    val allDownloads: MutableList<DownloadEntity>
        get() = downloadRepo.all

    fun getDownloadByUrl(data: String): MutableList<DownloadEntity> {
        return downloadRepo.getByUrl(data)
    }

    fun getByPath(data: String): MutableList<DownloadEntity> {
        return downloadRepo.getByPath(data)
    }

    fun insertSaves(entity: SaveEntity) {
        saveRepo.insert(entity)
    }

    fun deleteSaves(entity: SaveEntity) {
        saveRepo.delete(entity)
    }

    val allSaves: MutableList<SaveEntity>
        get() = saveRepo.all

    fun getSaveByVideoId(data: String): MutableList<SaveEntity> {
        return saveRepo.getByVideoId(data)
    }
}
