package com.water.alkaline.kengen.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.databinding.ActivityQurekaInterBinding;
import com.water.alkaline.kengen.placements.BackInterAds;
import com.water.alkaline.kengen.placements.InterAds;
import com.water.alkaline.kengen.utils.Constant;

public class QurekaInterActivity extends AppCompatActivity {

    public boolean isBackAds = false;
    boolean isClicked = false;

    ActivityQurekaInterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQurekaInterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent() != null && getIntent().hasExtra(Constant.BACK_ADS)) {
            isBackAds = getIntent().getBooleanExtra(Constant.BACK_ADS, true);
        }

        if (isBackAds)
            setQureka(this, binding.qurekaAds, binding.qurekaAds1, binding.gifInterRound, Constant.QBACKINTER_COUNT);
        else
            setQureka(this, binding.qurekaAds, binding.qurekaAds1, binding.gifInterRound, Constant.QINTER_COUNT);

        binding.rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBackAds) {
                    if (BackInterAds.mOnAdClosedListener != null)
                        BackInterAds.mOnAdClosedListener.onAdClosed();
                } else {
                    if (InterAds.mOnAdClosedListener != null)
                        InterAds.mOnAdClosedListener.onAdClosed();
                }
                Constant.gotoAds(QurekaInterActivity.this);
                finish();
            }
        });

        binding.qurekaAdsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void setQureka(Activity activity, ImageView imageViewMain, ImageView imageViewBG, ImageView imageViewGif, String data) {
        if (PowerPreference.getDefaultFile().getInt(data, 0) >= 5) {
            PowerPreference.getDefaultFile().putInt(data, 0);
            setQureka(activity, imageViewMain, imageViewBG, imageViewGif, data);
        } else {
            if (!activity.isFinishing()) {
                Glide.with(activity).load(Constant.adsQurekaInters[PowerPreference.getDefaultFile().getInt("qCount", 0)])
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViewBG);
                Glide.with(activity).load(Constant.adsQurekaInters[PowerPreference.getDefaultFile().getInt("qCount", 0)])
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViewMain);
                Glide.with(activity).asGif().load(Constant.adsQurekaGifInters[PowerPreference.getDefaultFile().getInt("qCount", 0)])
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViewGif);
            }
            int top = PowerPreference.getDefaultFile().getInt("qCount", 0) + 1;
            PowerPreference.getDefaultFile().putInt("qCount", top);
        }
    }

    @Override
    public void onBackPressed() {
        if (isBackAds) {
            if (BackInterAds.mOnAdClosedListener != null)
                BackInterAds.mOnAdClosedListener.onAdClosed();
        } else {
            if (InterAds.mOnAdClosedListener != null)
                InterAds.mOnAdClosedListener.onAdClosed();
        }
        finish();
    }
}