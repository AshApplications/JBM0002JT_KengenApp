package com.water.alkaline.kengen.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.databinding.ActivitySaveBinding;
import com.water.alkaline.kengen.library.ItemOffsetDecoration;
import com.water.alkaline.kengen.model.SaveEntity;
import com.water.alkaline.kengen.placements.BannerAds;
import com.water.alkaline.kengen.placements.InterAds;
import com.water.alkaline.kengen.ui.adapter.VideosAdapter;
import com.water.alkaline.kengen.ui.listener.OnVideoListener;
import com.water.alkaline.kengen.utils.Constant;
import com.preference.PowerPreference;

import java.util.ArrayList;
import java.util.List;

public class SaveActivity extends AppCompatActivity {

    public static SaveActivity saveActivity;
    ActivitySaveBinding binding;

    List<SaveEntity> list = new ArrayList<>();
    VideosAdapter adapter;
    public AppViewModel viewModel;

    @Override
    protected void onResume() {
        super.onResume();
        new BannerAds().showBanner(this);
    }

    public void setBG() {
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        Glide.with(this).load(R.drawable.bg_splash).diskCacheStrategy(DiskCacheStrategy.ALL)
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
        binding = ActivitySaveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setBG();
        saveActivity = this;

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

        binding.rvSaves.setLayoutManager(manager);
        binding.rvSaves.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.item_off_ten));

        adapter = new VideosAdapter(this, list, null, new OnVideoListener() {
            @Override
            public void onItemClick(int position, SaveEntity item) {
                new InterAds().showInter(SaveActivity.this, new InterAds.OnAdClosedListener() {
                    @Override
                    public void onAdClosed() {
                        int pos = position;
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).videoId.equalsIgnoreCase(item.videoId)) {
                                pos = i;
                                break;
                            }
                        }
                        PowerPreference.getDefaultFile().putString(Constant.mList, new Gson().toJson(list));
                        startActivity(new Intent(SaveActivity.this, PreviewActivity.class).putExtra(Constant.POSITION, pos));
                    }
                });

            }
        });
        binding.rvSaves.setAdapter(adapter);
        binding.rvSaves.getRecycledViewPool().setMaxRecycledViews(Constant.AD_TYPE, 50);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshActivity();
            }
        }, 500);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveActivity = null;
    }

    public void refreshActivity() {

        list.clear();
        list.addAll(viewModel.getAllSaves());
        adapter.refreshAdapter(list);
        binding.includedProgress.progress.setVisibility(View.GONE);
        checkData();
    }

    public void refreshData() {
        list.clear();
        list.addAll(viewModel.getAllSaves());
        adapter.refreshAdapter(list);
        binding.includedProgress.progress.setVisibility(View.GONE);
        checkData();
    }

    public void checkData() {
        if (binding.rvSaves.getAdapter() != null && binding.rvSaves.getAdapter().getItemCount() > 0) {
            binding.includedProgress.llError.setVisibility(View.GONE);
        } else {
            binding.includedProgress.llError.setVisibility(View.VISIBLE);
        }
    }

}