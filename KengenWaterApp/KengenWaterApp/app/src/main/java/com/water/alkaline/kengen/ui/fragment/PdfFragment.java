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

import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.databinding.FragmentPdfBinding;
import com.water.alkaline.kengen.model.main.Pdf;
import com.water.alkaline.kengen.ui.activity.HomeActivity;
import com.water.alkaline.kengen.ui.activity.PdfActivity;
import com.water.alkaline.kengen.ui.adapter.PdfAdapter;
import com.water.alkaline.kengen.ui.listener.OnPdfListener;
import com.water.alkaline.kengen.utils.Constant;
import com.water.alkaline.kengen.utils.uiController;


import java.util.ArrayList;
import java.util.List;


public class PdfFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    FragmentPdfBinding binding;
    Activity activity;

    List<Pdf> list = new ArrayList<>();
    PdfAdapter adapter;

    public AppViewModel viewModel;

    public PdfFragment() {
    }

    public PdfFragment(Activity activity) {
        this.activity = activity;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (activity == null) {
            activity = (HomeActivity) context;
        }
    }

    public static PdfFragment newInstance(Activity activity, String param1) {
        PdfFragment fragment = new PdfFragment(activity);
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
        binding = FragmentPdfBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        if (activity != null) {
            adapter = new PdfAdapter(activity, list, (position, item) -> uiController.gotoIntent(activity, new Intent(activity, PdfActivity.class).putExtra("mpath", item.getUrl()), true, false));
            GridLayoutManager manager = new GridLayoutManager(activity, 2);
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
            binding.rvPdfs.setLayoutManager(manager);
            binding.rvPdfs.setAdapter(adapter);
            binding.rvPdfs.setItemViewCacheSize(100);
            refreshFragment();
        }
    }

    public void refreshFragment() {
        if (adapter != null) {
            new Handler().postDelayed(() -> {
                adapter.refreshAdapter(viewModel.getAllPdfByCategory(mParam1));
                binding.includedProgress.progress.setVisibility(View.GONE);
                checkData();
            }, 500);
        }
    }

    public void checkData() {
        if (binding.rvPdfs.getAdapter() != null && binding.rvPdfs.getAdapter().getItemCount() > 0) {
            binding.includedProgress.llError.setVisibility(View.GONE);
        } else {
            binding.includedProgress.llError.setVisibility(View.VISIBLE);
        }
    }
}