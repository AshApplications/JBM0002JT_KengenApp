package com.water.alkaline.kengen.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

public class ViewToImage {
    Context context;
    ActionListeners listeners;
    View view;
    Bitmap bitmap = null;
    String filePath = null;

    public ViewToImage(Context context, View view) {
        this.context = context;
        this.view = view;
        this.convert();
    }

    public ViewToImage(Context context, View view, ActionListeners listeners) {
        this.context = context;
        this.listeners = listeners;
        this.view = view;
        this.convert();
    }

    public ViewToImage(Context context, Bitmap view, ActionListeners listeners) {
        this.context = context;
        this.bitmap = view;
        this.listeners = listeners;
        saveTheImage(view);
    }

    private void convert() {
        Bitmap bitmap = this.getBitmapFromView(this.view, this.view.getWidth(), this.view.getHeight());
        this.saveTheImage(bitmap);
    }

    private Bitmap getBitmapFromView(View view, int width, int height) {
        this.bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas mCanvas = new Canvas(this.bitmap);
        view.layout(0, 0, view.getLayoutParams().width, view.getLayoutParams().height);
        view.draw(mCanvas);
        return this.bitmap;
    }

    public String getImagedisc() {
        File dCimDirPath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

        if (!dCimDirPath.exists())
            dCimDirPath.mkdir();
        File myCreationDir = new File(dCimDirPath, "KenGen/SavedIamges");
        if (!myCreationDir.exists())
            myCreationDir.mkdirs();

        return String.valueOf(myCreationDir);
    }


    private void saveTheImage(Bitmap finalBitmap) {

        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(getImagedisc(), fileName);
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            this.filePath = getImagedisc() + "/" + fileName;

            if (this.listeners != null) {
                this.listeners.convertedWithSuccess(this.bitmap, this.filePath);
            }
        } catch (Exception var9) {
            var9.printStackTrace();
            if (this.listeners != null) {
                this.listeners.convertedWithError(var9.getMessage());
            }
        }

    }
}

