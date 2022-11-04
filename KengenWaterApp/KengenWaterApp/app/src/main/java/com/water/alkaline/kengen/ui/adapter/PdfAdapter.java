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
import com.google.gms.ads.AdUtils;
import com.google.gms.ads.databinding.LayoutAdLargeBinding;
import com.google.gms.ads.databinding.LayoutAdMediumBinding;
import com.google.gms.ads.databinding.LayoutAdMiniBinding;
import com.water.alkaline.kengen.databinding.ItemImageBinding;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.model.main.Banner;
import com.water.alkaline.kengen.model.main.Pdf;
import com.google.gms.ads.MainAds;
import com.water.alkaline.kengen.ui.listener.OnPdfListener;
import com.water.alkaline.kengen.ui.viewholder.LargeAdHolder;
import com.water.alkaline.kengen.ui.viewholder.MediumAdHolder;
import com.water.alkaline.kengen.ui.viewholder.MiniAdHolder;
import com.water.alkaline.kengen.utils.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PdfAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    List<Pdf> arrayList = new ArrayList<>();
    OnPdfListener listener;

    HashMap<Integer, Integer> hashMap = new HashMap<>();

    public PdfAdapter(Activity activity, List<Pdf> arrayList, OnPdfListener listener) {
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
        int PARTICLE_AD_DISPLAY_COUNT = PowerPreference.getDefaultFile().getInt(AdUtils.ListNativeAfterCount, 10);
        if (PARTICLE_AD_DISPLAY_COUNT > 0) {

            arrayList.removeAll(Collections.singleton(null));
            ArrayList<Pdf> tempArr = new ArrayList<>();

            if (arrayList.size() > 0) {
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
        if (arrayList.get(position) == null)
            return Constant.AD_TYPE;
        else return Constant.STORE_TYPE;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Constant.STORE_TYPE)
            return new ViewHolder(ItemImageBinding.inflate(LayoutInflater.from(activity), parent, false));
        else {
            if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneListNative, 0) == 1)
                return new LargeAdHolder(LayoutAdLargeBinding.inflate(LayoutInflater.from(activity), parent, false));
            else if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneListNative, 0) == 2)
                return new MiniAdHolder(LayoutAdMiniBinding.inflate(LayoutInflater.from(activity), parent, false));
            else if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneListNative, 0) == 3)
                return new MediumAdHolder(LayoutAdMediumBinding.inflate(LayoutInflater.from(activity), parent, false));
            else
                return new MiniAdHolder(LayoutAdMiniBinding.inflate(LayoutInflater.from(activity), parent, false));
        }
    }

    public void refreshAdapter(List<Pdf> arrayList) {
        this.arrayList = arrayList;
        setAds(true);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            Glide.with(activity).load(arrayList.get(position).getImage())
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
        } else if (holder instanceof LargeAdHolder) {
            LargeAdHolder adHolder = (LargeAdHolder) holder;
            new MainAds().showListNativeAds(activity, adHolder.binding.adFrameLarge, adHolder.binding.adSpaceLarge);
            int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics());
            ((LargeAdHolder) holder).binding.adFrameLarge.setPadding(0, dp, dp, 0);
        } else if (holder instanceof MiniAdHolder) {
            MiniAdHolder adHolder = (MiniAdHolder) holder;
            new MainAds().showListNativeAds(activity, adHolder.binding.adFrameLarge, adHolder.binding.adSpaceLarge);
            int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics());
            ((MiniAdHolder) holder).binding.adFrameLarge.setPadding(0, dp, dp, 0);
        } else if (holder instanceof MediumAdHolder) {
            MediumAdHolder adHolder = (MediumAdHolder) holder;
            new MainAds().showListNativeAds(activity, adHolder.binding.adFrameLarge, adHolder.binding.adSpaceLarge);
            int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics());
            ((MediumAdHolder) holder).binding.adFrameLarge.setPadding(0, dp, dp, 0);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
