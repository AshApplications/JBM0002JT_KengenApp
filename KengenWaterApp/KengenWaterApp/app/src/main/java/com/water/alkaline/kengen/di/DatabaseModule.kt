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
    fun provideDb( @ApplicationContext context: Context): AppDB {
        return Room.databaseBuilder(
            context,
            AppDB::class.java, "kangenWaterApp"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build();
    }
}