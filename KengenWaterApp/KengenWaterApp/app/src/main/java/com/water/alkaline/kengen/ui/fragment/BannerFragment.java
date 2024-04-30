package com.water.alkaline.kengen.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.databinding.FragmentBannerBinding;
import com.water.alkaline.kengen.model.main.Banner;
import com.water.alkaline.kengen.ui.activity.HomeActivity;
import com.water.alkaline.kengen.ui.activity.BannerActivity;
import com.water.alkaline.kengen.ui.adapter.BannerAdapter;
import com.water.alkaline.kengen.utils.Constant;
import com.water.alkaline.kengen.utils.uiController;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class BannerFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    FragmentBannerBinding binding;
    Activity activity;

    List<Banner> list = new ArrayList<>();
    BannerAdapter adapter;
    public AppViewModel viewModel;


    public BannerFragment() {
    }

    public BannerFragment(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (activity == null) {
            activity = (HomeActivity) context;
        }
    }

    public static BannerFragment newInstance(Activity activity, String param1) {
        BannerFragment fragment = new BannerFragment(activity);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanseState) {
        binding = FragmentBannerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        if (activity != null) {
            adapter = new BannerAdapter(activity, list, (position, item) -> {
                list.removeAll(Collections.singleton(null));
                int pos = position;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId().equalsIgnoreCase(item.getId())) {
                        pos = i;
                        break;
                    }
                }
                PowerPreference.getDefaultFile().putString(Constant.mBanners, new Gson().toJson(list));
                Intent intent = new Intent(activity, BannerActivity.class);
                intent.putExtra("POS", pos);
                intent.putExtra("PAGE", Constant.LIVE);
                uiController.gotoIntent(activity, intent, true, false);
            });
            GridLayoutManager manager = new GridLayoutManager(activity, 2);
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int i) {
                    switch (adapter.getItemViewType(i)) {
                        case Constant.STORE_TYPE:
                            return 1;
                        case Constant.AD_TYPE:
                            return 2;
                        default:
                            return 1;

                    }
                }
            });
            binding.rvBanners.setLayoutManager(manager);
            binding.rvBanners.setAdapter(adapter);
            binding.rvBanners.setItemViewCacheSize(100);
            refreshFragment();
        }
    }

    public void refreshFragment() {
        if (adapter != null) {
            new Handler().postDelayed(() -> {
                list = viewModel.getAllBannerByCategory(mParam1);
                adapter.refreshAdapter(list);
                binding.includedProgress.progress.setVisibility(View.GONE);
                checkData();
            }, 500);
        }
    }

    public void checkData() {
        if (binding.rvBanners.getAdapter() != null && binding.rvBanners.getAdapter().getItemCount() > 0) {
            binding.includedProgress.llError.setVisibility(View.GONE);
        } else {
            binding.includedProgress.llError.setVisibility(View.VISIBLE);
        }
    }
}