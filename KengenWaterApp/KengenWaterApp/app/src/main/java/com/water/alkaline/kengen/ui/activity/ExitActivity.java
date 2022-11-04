package com.water.alkaline.kengen.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gms.ads.AdUtils;
import com.google.gms.ads.InterAds;
import com.google.gms.ads.OpenAds;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.R;
import com.google.gms.ads.MainAds;
import com.water.alkaline.kengen.library.downloader.core.Core;
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
        if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneSplashAppOpen, 0) == 1) {
            new MainAds().showSplashInterAds(ExitActivity.this, new InterAds.OnAdClosedListener() {
                @Override
                public void onAdClosed() {
                   exit();
                }
            });
        } else if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneSplashAppOpen, 0) == 2) {
            new MainAds().showOpenAds(ExitActivity.this, new OpenAds.OnAdClosedListener() {
                @Override
                public void onAdClosed() {
                    exit();
                }
            });
        } else {
            exit();
        }
    }

    public void exit() {
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
        },2000);
    }
}