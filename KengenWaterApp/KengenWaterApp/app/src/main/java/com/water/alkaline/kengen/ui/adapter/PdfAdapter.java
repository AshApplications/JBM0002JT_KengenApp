package com.water.alkaline.kengen.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.water.alkaline.kengen.databinding.ItemFeedbackBinding;
import com.water.alkaline.kengen.databinding.ItemImageBinding;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.databinding.NativeAdLargeBinding;
import com.water.alkaline.kengen.databinding.NativeAdMiniBinding;
import com.water.alkaline.kengen.model.main.Pdf;
import com.water.alkaline.kengen.placements.ListNativeAds;
import com.water.alkaline.kengen.ui.listener.OnPdfListener;
import com.water.alkaline.kengen.ui.viewholder.LargeAdHolder;
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
        setAds();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemImageBinding binding;

        public ViewHolder(ItemImageBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }


    public void setAds() {
        int PARTICLE_AD_DISPLAY_COUNT = PowerPreference.getDefaultFile().getInt(Constant.ListNativeAfterCount, 10);
        arrayList.removeAll(Collections.singleton(null));
        ArrayList<Pdf> tempArr = new ArrayList<>();
        if (arrayList.size() > 0) {
            tempArr.add(null);
        }

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.size() > PARTICLE_AD_DISPLAY_COUNT) {
                if (i != 0) {
                    if (i % PARTICLE_AD_DISPLAY_COUNT == 0) {
                        tempArr.add(null);
                    }
                }
                tempArr.add(arrayList.get(i));
            } else {
                tempArr.add(arrayList.get(i));
            }
        }
        if (arrayList.size() > 0) {
            if (arrayList.size() % PARTICLE_AD_DISPLAY_COUNT == 0) {
                tempArr.add(null);
            }
        }

        this.arrayList = tempArr;
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
            if (PowerPreference.getDefaultFile().getInt(Constant.ListNativeWhichOne, 0) == 0)
                return new LargeAdHolder(NativeAdLargeBinding.inflate(LayoutInflater.from(activity), parent, false));
            else
                return new MiniAdHolder(NativeAdMiniBinding.inflate(LayoutInflater.from(activity), parent, false));
        }
    }

    public void refreshAdapter(List<Pdf> arrayList) {
        this.arrayList = arrayList;
        setAds();
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
            new ListNativeAds().showListNativeAds(activity, adHolder.binding.frameNativeLarge, adHolder.binding.adSpaceLarge);
        } else if (holder instanceof MiniAdHolder) {
            MiniAdHolder adHolder = (MiniAdHolder) holder;
            new ListNativeAds().showListNativeAds(activity, adHolder.binding.frameNativeMini, adHolder.binding.adSpaceMini);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
