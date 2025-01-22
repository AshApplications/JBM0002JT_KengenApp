package com.water.alkaline.kengen.ui.activity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.preference.PowerPreference
import com.water.alkaline.kengen.R
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.delayTask

class ExitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exit)
        window.setLayout(-1, -1)
        window.setBackgroundDrawable(null)
        setFinishOnTouchOutside(false)
        delayTask {
            checkAds(this@ExitActivity)
        }
    }

    private fun checkAds(activity: Activity?) {
        /*if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneSplashAppOpen, 0) == 1) {
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
        }*/
    }

    fun exit() {
        PowerPreference.getDefaultFile().putBoolean(Constant.isRunning, false)
        delayTask {
            try {
                System.exit(0)
            } catch (e: Exception) {
                e.printStackTrace()
                finishAffinity()
            }
        }
    }
}