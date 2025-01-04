package com.water.alkaline.kengen.di

import android.content.Context
import androidx.room.Room
import com.water.alkaline.kengen.data.db.AppDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDB {
        return Room.databaseBuilder(
            context,
            AppDB::class.java, "kangenWaterApp"
        ).fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build();
    }


    @Provides
    @Singleton
    fun provideBannerDao(db: AppDB) = db.bannerDao()

    @Provides
    @Singleton
    fun provideCategodyDao(db: AppDB) = db.categoryDao()


    @Provides
    @Singleton
    fun provideChannelDao(db: AppDB) = db.channelDao()

    @Provides
    @Singleton
    fun provideDownloadDao(db: AppDB) = db.downloadDao()

    @Provides
    @Singleton
    fun providePdfDao(db: AppDB) = db.pdfDao()


    @Provides
    @Singleton
    fun provideSaveDao(db: AppDB) = db.saveDao()

    @Provides
    @Singleton
    fun provideSubcatDao(db: AppDB) = db.subcatDao()

}