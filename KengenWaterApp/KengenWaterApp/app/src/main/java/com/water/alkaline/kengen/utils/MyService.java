package com.water.alkaline.kengen.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.preference.PowerPreference;

public class MyService extends Service {
    private String TAG = MyService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e(TAG, "onTaskRemoved");
        PowerPreference.getDefaultFile().putBoolean(Constant.isRunning, false);
        stopSelf();
     }

    @Override
    public void onDestroy() {
        Log.e(TAG, "Service Destroyed");
        PowerPreference.getDefaultFile().putBoolean(Constant.isRunning, false);
        stopSelf();
        super.onDestroy();
    }
}
