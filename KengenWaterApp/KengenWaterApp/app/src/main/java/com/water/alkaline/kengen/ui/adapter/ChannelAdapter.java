package com.water.alkaline.kengen.ui.adapter;


import android.app.Activity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gms.ads.AdLoader;
import com.google.gms.ads.MyApp;
import com.google.gms.ads.databinding.LayoutAdUniversalBinding;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.MyApplication;
import com.google.gms.ads.AdUtils;
import com.water.alkaline.kengen.databinding.ItemImageBinding;
import com.water.alkaline.kengen.model.main.Banner;
import com.water.alkaline.kengen.model.main.Channel;
import com.water.alkaline.kengen.ui.listener.OnChannelListener;
import com.water.alkaline.kengen.utils.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    List<Channel> arrayList = new ArrayList<>();
    OnChannelListener listener;

    public ChannelAdapter(Activity activity, List<Channel> arrayList, OnChannelListener listener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.listener = listener;
        setAds(false);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemImageBinding binding;

        public ViewHolder(ItemImageBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }


    public void setAds(boolean isAds) {
        int PARTICLE_AD_DISPLAY_COUNT = MyApp.getAdModel().getAdsListViewCount();

        if (PARTICLE_AD_DISPLAY_COUNT > 0 && MyApp.getAdModel().getAdsOnOff().equalsIgnoreCase("Yes")) {

            arrayList.removeAll(Collections.singleton(null));
            ArrayList<Channel> tempArr = new ArrayList<>();

            if (!arrayList.isEmpty()) {
                tempArr.add(null);
            }

            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.size() > PARTICLE_AD_DISPLAY_COUNT) {
                    if (i != 0 && i % PARTICLE_AD_DISPLAY_COUNT == 0) {
                        tempArr.add(null);
                    }
                    tempArr.add(arrayList.get(i));
                } else {
                    tempArr.add(arrayList.get(i));
                }
            }

            this.arrayList = tempArr;
        }

        if (isAds)
            notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return arrayList.get(position) == null ? Constant.AD_TYPE : Constant.STORE_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Constant.AD_TYPE)
            return new AdHolder(LayoutAdUniversalBinding.inflate(LayoutInflater.from(activity), parent, false));
        else
            return new ViewHolder(ItemImageBinding.inflate(LayoutInflater.from(activity), parent, false));
    }

    public void refreshAdapter(List<Channel> arrayList) {
        this.arrayList = arrayList;
        setAds(true);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AdHolder) {
            AdHolder adHolder = (AdHolder) holder;
            if (adHolder.binding.flAd.getChildCount() <= 0) {
                AdLoader.getInstance().showNativeList(activity, adHolder.binding);
            }
        } else {
            ViewHolder viewHolder = (ViewHolder) holder;
            Glide.with(activity).load(arrayList.get(position).getUrl())
                    .placeholder(MyApplication.getPlaceHolder())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.binding.imgVideo);
            viewHolder.binding.txtVideoTitle.setText(arrayList.get(position).getName());
            viewHolder.binding.txtVideoTitle.setSelected(true);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(holder.getAdapterPosition(), arrayList.get(holder.getAdapterPosition()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}


