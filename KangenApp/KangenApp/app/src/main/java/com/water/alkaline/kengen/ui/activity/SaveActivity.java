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
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.databinding.ActivitySaveBinding;
import com.water.alkaline.kengen.library.ItemOffsetDecoration;
import com.water.alkaline.kengen.model.SaveEntity;
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
        binding = ActivitySaveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setBG();
        saveActivity = this;
        adapter = new VideosAdapter(this, list,null, new OnVideoListener() {
            @Override
            public void onItemClick(int position, SaveEntity item) {
                PowerPreference.getDefaultFile().putString(Constant.mList, new Gson().toJson(list));
                PowerPreference.getDefaultFile().putBoolean(Constant.isSaved, true);
                PowerPreference.getDefaultFile().putInt(Constant.mPosition, position);
                startActivity(new Intent(SaveActivity.this, PlayerActivity.class));
            }
        });
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.rvSaves.setLayoutManager(manager);
        binding.rvSaves.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.item_off_ten));
        binding.rvSaves.setAdapter(adapter);
        refreshActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveActivity = null;
    }

    public void refreshActivity() {
        if (binding.rvSaves.getAdapter().getItemCount() <= 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    list.clear();
                    list.addAll(viewModel.getAllSaves());
                    adapter.refreshAdapter(list);
                    binding.includedProgress.progress.setVisibility(View.GONE);
                    checkData();
                }
            }, 500);
        }
    }

    public void refreshData() {
        list.clear();
        list.addAll(viewModel.getAllSaves());
        adapter.refreshAdapter(list);
        binding.includedProgress.progress.setVisibility(View.GONE);
        checkData();
    }

    public void checkData() {
        if (binding.rvSaves.getAdapter().getItemCount() > 0) {
            binding.includedProgress.llError.setVisibility(View.GONE);
        } else {
            binding.includedProgress.llError.setVisibility(View.VISIBLE);
        }
    }

}