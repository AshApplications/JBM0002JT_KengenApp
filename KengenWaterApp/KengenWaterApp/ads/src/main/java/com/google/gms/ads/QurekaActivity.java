package com.google.gms.ads;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gms.ads.databinding.ActivityQurekaBinding;


public class QurekaActivity extends AppCompatActivity {

    ActivityQurekaBinding binding;
    public boolean isBackAds = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQurekaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent() != null && getIntent().hasExtra(AdUtils.BACK_ADS)) {
            isBackAds = getIntent().getBooleanExtra(AdUtils.BACK_ADS, true);
        }

        AdUtils.setQureka(this, AdUtils.QINTER_COUNT, binding.qurekaAds, binding.qurekaAds1, binding.gifInterRound);
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
                AdUtils.gotoAds(QurekaActivity.this);
                finish();
            }
        });

        binding.qurekaAdsClose.setOnClickListener(v -> onBackPressed());
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