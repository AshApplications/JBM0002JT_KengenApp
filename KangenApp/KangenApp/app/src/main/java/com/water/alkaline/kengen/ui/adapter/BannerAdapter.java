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
import com.water.alkaline.kengen.library.ViewAnimator.ViewAnimator;
import com.water.alkaline.kengen.model.main.Banner;
import com.water.alkaline.kengen.ui.listener.OnBannerListerner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    List<Banner> arrayList = new ArrayList<>();
    OnBannerListerner listener;



    public BannerAdapter(Activity activity, List<Banner> arrayList, OnBannerListerner listener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemVideoBinding binding;

        public ViewHolder(ItemVideoBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemVideoBinding.inflate(LayoutInflater.from(activity), parent, false));
    }

    public void refreshAdapter(List<Banner> arrayList) {
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
       ViewHolder viewHolder = (ViewHolder) holder;
        Glide.with(activity).load(arrayList.get(position).getUrl())
                .placeholder(MyApplication.getPlaceHolder())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.binding.imgVideo);
        viewHolder.binding.txtVideoTitle.setText(arrayList.get(position).getName());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.getAdapterPosition(), arrayList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}

