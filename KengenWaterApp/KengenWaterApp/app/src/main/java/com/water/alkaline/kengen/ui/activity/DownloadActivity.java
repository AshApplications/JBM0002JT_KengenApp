package com.water.alkaline.kengen.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.gms.ads.AdLoader;
import com.google.gms.ads.MyApp;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.databinding.ActivityDownloadBinding;
import com.water.alkaline.kengen.model.DownloadEntity;
import com.water.alkaline.kengen.ui.adapter.DownloadAdapter;
import com.water.alkaline.kengen.ui.listener.OnDownloadListener;
import com.water.alkaline.kengen.utils.Constant;
import com.water.alkaline.kengen.utils.uiController;

import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends AppCompatActivity {

    ActivityDownloadBinding binding;

    List<DownloadEntity> list = new ArrayList<>();
    DownloadAdapter adapter;
    public AppViewModel viewModel;

    @Override
    public void onBackPressed() {
        uiController.onBackPressed(this);
    }

    public void setBG() {
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        binding.includedToolbar.ivBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApp.getAdModel().getAdsOnOff().equalsIgnoreCase("Yes")) {
            if (binding.includedAd.flAd.getChildCount() <= 0) {
                AdLoader.getInstance().showUniversalAd(this, binding.includedAd, false);
            }
        } else {
            binding.includedAd.cvAdMain.setVisibility(View.GONE);
            binding.includedAd.flAd.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDownloadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setBG();

        adapter = new DownloadAdapter(this, list, (position, item) -> {
            uiController.gotoIntent(this, new Intent(DownloadActivity.this, item.type == Constant.TYPE_PDF ? PdfActivity.class : ImageActivity.class).putExtra("mpath", item.type == Constant.TYPE_PDF ? item.filePath : item.url), true, false);
        });
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                switch (adapter.getItemViewType(i)) {
                    case Constant.STORE_TYPE:
                        return 1;
                    case Constant.AD_TYPE:
                        return 2;
                    case Constant.LOADING:
                        return 1;
                    default:
                        return 1;

                }
            }
        });
        binding.rvDownloads.setLayoutManager(manager);
        binding.rvDownloads.setAdapter(adapter);
        binding.rvDownloads.setItemViewCacheSize(100);
        refreshActivity();
    }

    public void refreshActivity() {
        if (binding.rvDownloads.getAdapter().getItemCount() <= 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.refreshAdapter(viewModel.getAllDownloads());
                    binding.includedProgress.progress.setVisibility(View.GONE);
                    checkData();
                }
            }, 500);
        }
    }

    public void checkData() {
        if (binding.rvDownloads.getAdapter() != null && binding.rvDownloads.getAdapter().getItemCount() > 0) {
            binding.includedProgress.llError.setVisibility(View.GONE);
        } else {
            binding.includedProgress.llError.setVisibility(View.VISIBLE);
        }
    }

}