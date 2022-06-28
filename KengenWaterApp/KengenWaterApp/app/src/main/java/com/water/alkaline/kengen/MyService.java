package com.water.alkaline.kengen;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.water.alkaline.kengen.utils.Constant;
import com.preference.PowerPreference;

public class MyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        PowerPreference.getDefaultFile().putBoolean(Constant.isRunning,false);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PowerPreference.getDefaultFile().putBoolean(Constant.isRunning,false);
    }
}
