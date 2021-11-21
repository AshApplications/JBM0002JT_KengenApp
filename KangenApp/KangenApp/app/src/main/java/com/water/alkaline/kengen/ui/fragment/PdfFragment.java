package com.water.alkaline.kengen.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.databinding.FragmentPdfBinding;
import com.water.alkaline.kengen.library.ItemOffsetDecoration;
import com.water.alkaline.kengen.model.main.Pdf;
import com.water.alkaline.kengen.placements.InterAds;
import com.water.alkaline.kengen.ui.activity.PdfActivity;
import com.water.alkaline.kengen.ui.activity.PlayerActivity;
import com.water.alkaline.kengen.ui.adapter.PdfAdapter;
import com.water.alkaline.kengen.ui.listener.OnPdfListener;
import com.water.alkaline.kengen.utils.Constant;

import org.jetbrains.annotations.NotNull;

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

    public PdfFragment(Activity activity) {
        this.activity = activity;
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
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanseState) {
        binding = FragmentPdfBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        adapter = new PdfAdapter(activity, list, new OnPdfListener() {
            @Override
            public void onItemClick(int position, Pdf item) {
                new InterAds().showInter(activity, new InterAds.OnAdClosedListener() {
                    @Override
                    public void onAdClosed() {

                        startActivity(new Intent(activity, PdfActivity.class).putExtra("mpath", item.getUrl()));
                    }
                });

            }
        });
        GridLayoutManager manager = new GridLayoutManager(activity,2);
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
        binding.rvPdfs.addItemDecoration(new ItemOffsetDecoration(activity, R.dimen.item_off_ten));
        binding.rvPdfs.setAdapter(adapter);
        binding.rvPdfs.getRecycledViewPool().setMaxRecycledViews(Constant.AD_TYPE, 50);
        refreshFragment();
    }

    public void refreshFragment() {
        if (binding.rvPdfs.getAdapter().getItemCount() <= 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.refreshAdapter(viewModel.getAllPdfByCategory(mParam1));
                    binding.includedProgress.progress.setVisibility(View.GONE);
                    checkData();
                }
            }, 500);
        }
    }

    public void checkData() {
        if (binding.rvPdfs.getAdapter().getItemCount() > 0) {
            binding.includedProgress.llError.setVisibility(View.GONE);
        } else {
            binding.includedProgress.llError.setVisibility(View.VISIBLE);
        }
    }
}