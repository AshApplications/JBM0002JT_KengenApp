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
            Constant.setQureka(this, binding.qurekaAds, binding.qurekaAds1, binding.gifInterRound, Constant.QBACKINTER_COUNT);
        else
            Constant.setQureka(this, binding.qurekaAds, binding.qurekaAds1, binding.gifInterRound, Constant.QINTER_COUNT);

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