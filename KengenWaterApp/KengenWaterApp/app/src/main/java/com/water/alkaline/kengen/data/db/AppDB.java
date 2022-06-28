package com.water.alkaline.kengen.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.water.alkaline.kengen.data.db.dao.BannerDao;
import com.water.alkaline.kengen.data.db.dao.CategoryDao;
import com.water.alkaline.kengen.data.db.dao.ChannelDao;
import com.water.alkaline.kengen.data.db.dao.DownloadDao;
import com.water.alkaline.kengen.data.db.dao.PdfDao;
import com.water.alkaline.kengen.data.db.dao.SaveDao;
import com.water.alkaline.kengen.data.db.dao.SubcatDao;
import com.water.alkaline.kengen.model.DownloadEntity;
import com.water.alkaline.kengen.model.SaveEntity;
import com.water.alkaline.kengen.model.main.Banner;
import com.water.alkaline.kengen.model.main.Category;
import com.water.alkaline.kengen.model.main.Channel;
import com.water.alkaline.kengen.model.main.Pdf;
import com.water.alkaline.kengen.model.main.Subcategory;


@Database(entities = {Category.class, Subcategory.class, Channel.class, Pdf.class, Banner.class, DownloadEntity.class, SaveEntity.class}, version = 2, exportSchema = false)
public abstract class AppDB extends RoomDatabase {

    private static AppDB instance;

    public abstract CategoryDao categoryDao();
    public abstract SubcatDao subcatDao();
    public abstract ChannelDao channelDao();
    public abstract PdfDao pdfDao();
    public abstract BannerDao bannerDao();
    public abstract DownloadDao downloadDao();
    public abstract SaveDao saveDao();

    public static synchronized AppDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                     AppDB.class, "kangenWaterApp")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}