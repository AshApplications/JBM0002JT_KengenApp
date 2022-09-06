package com.water.alkaline.kengen.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.github.shadowsocks.Core;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.placements.InterAds;
import com.water.alkaline.kengen.placements.InterSplashAds;
import com.water.alkaline.kengen.placements.MainAds;
import com.water.alkaline.kengen.placements.OpenSplashAds;
import com.water.alkaline.kengen.utils.Constant;

public class ExitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);

        getWindow().setLayout(-1, -1);
        getWindow().setBackgroundDrawable(null);
        setFinishOnTouchOutside(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAds(ExitActivity.this);
            }
        }, 2000);

    }

    public void checkAds(Activity activity) {
        if (getIntent().getBooleanExtra("isAds", false) && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleExitSplashInterOnOff, false)) {

            if (PowerPreference.getDefaultFile().getInt(Constant.AppOpen, 1) == 1) {
                new MainAds().showOpenAds(activity, new OpenSplashAds.OnAdClosedListener() {
                    @Override
                    public void onAdClosed() {
                        exit();
                    }
                });
            } else if (PowerPreference.getDefaultFile().getInt(Constant.AppOpen, 1) == 2) {
                new MainAds().showSplashInterAds(activity, new InterSplashAds.OnAdClosedListener() {
                    @Override
                    public void onAdClosed() {
                        exit();
                    }
                });
            } else {
                exit();
            }
        } else {
            exit();
        }
    }

    public void exit() {

        Core.INSTANCE.stopService();
        PowerPreference.getDefaultFile().putBoolean(Constant.isRunning, false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    finishAffinity();
                }
            }
        },1000);

    }
}