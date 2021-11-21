package com.water.alkaline.kengen.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.databinding.ActivityDownloadBinding;
import com.water.alkaline.kengen.library.ItemOffsetDecoration;
import com.water.alkaline.kengen.model.DownloadEntity;
import com.water.alkaline.kengen.placements.BannerAds;
import com.water.alkaline.kengen.placements.InterAds;
import com.water.alkaline.kengen.ui.adapter.DownloadAdapter;
import com.water.alkaline.kengen.ui.listener.OnDownloadListener;
import com.water.alkaline.kengen.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends AppCompatActivity {

    ActivityDownloadBinding binding;

    List<DownloadEntity> list = new ArrayList<>();
    DownloadAdapter adapter;
    public AppViewModel viewModel;

    @Override
    protected void onResume() {
        super.onResume();
        new BannerAds().showBanner(this);
    }
    public void setBG() {
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        Glide.with(this).load(R.drawable.bg).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivBG);


        binding.includedToolbar.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDownloadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setBG();

        adapter = new DownloadAdapter(this, list, new OnDownloadListener() {
            @Override
            public void onItemClick(int position, DownloadEntity item) {

                new InterAds().showInter(DownloadActivity.this, new InterAds.OnAdClosedListener() {
                    @Override
                    public void onAdClosed() {
                        if (item.type == Constant.TYPE_PDF) {
                            startActivity(new Intent(DownloadActivity.this, PdfActivity.class).putExtra("mpath", item.filePath));
                        } else {
                            startActivity(new Intent(DownloadActivity.this, ImageActivity.class).putExtra("mpath", item.url));
                        }
                    }
                });


            }
        });
        GridLayoutManager manager = new GridLayoutManager(this,2);
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
        binding.rvDownloads.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.item_off_ten));
        binding.rvDownloads.setAdapter(adapter);
        binding.rvDownloads.getRecycledViewPool().setMaxRecycledViews(Constant.AD_TYPE, 50);
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
        if (binding.rvDownloads.getAdapter().getItemCount() > 0) {
            binding.includedProgress.llError.setVisibility(View.GONE);
        } else {
            binding.includedProgress.llError.setVisibility(View.VISIBLE);
        }
    }

}