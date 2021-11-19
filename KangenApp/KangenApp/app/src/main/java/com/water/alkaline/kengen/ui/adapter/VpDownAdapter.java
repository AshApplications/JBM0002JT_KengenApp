package com.water.alkaline.kengen.ui.adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.databinding.ItemVideoBinding;
import com.water.alkaline.kengen.databinding.ItemVpBinding;
import com.water.alkaline.kengen.model.SaveEntity;
import com.water.alkaline.kengen.model.main.Banner;
import com.water.alkaline.kengen.ui.listener.OnBannerListerner;

import java.util.ArrayList;
import java.util.List;

public class VpDownAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    List<SaveEntity> arrayList = new ArrayList<>();
    OnBannerListerner listener;

    public VpDownAdapter(Activity activity, List<SaveEntity> arrayList, OnBannerListerner listener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemVpBinding binding;

        public ViewHolder(ItemVpBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemVpBinding.inflate(LayoutInflater.from(activity), parent, false));
    }

    public void refreshAdapter(List<SaveEntity> arrayList) {
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;

        Glide.with(activity).load(arrayList.get(position).imgUrl)
                .placeholder(MyApplication.getPlaceHolder())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.binding.ivImage);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}

