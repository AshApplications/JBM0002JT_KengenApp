package com.water.alkaline.kengen.ui.adapter;


import android.app.Activity;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gms.ads.databinding.LayoutAdLargeBinding;
import com.google.gms.ads.databinding.LayoutAdMediumBinding;
import com.google.gms.ads.databinding.LayoutAdMiniBinding;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.MyApplication;
import com.google.gms.ads.AdUtils;
import com.water.alkaline.kengen.databinding.ItemVideoBinding;
import com.water.alkaline.kengen.databinding.LayoutProgressBinding;
import com.water.alkaline.kengen.model.SaveEntity;
import com.google.gms.ads.MainAds;
import com.water.alkaline.kengen.model.main.Banner;
import com.water.alkaline.kengen.ui.activity.PlayerActivity;
import com.water.alkaline.kengen.ui.listener.OnLoadMoreListener;
import com.water.alkaline.kengen.ui.listener.OnVideoListener;
import com.water.alkaline.kengen.ui.viewholder.LargeAdHolder;
import com.water.alkaline.kengen.ui.viewholder.MediumAdHolder;
import com.water.alkaline.kengen.ui.viewholder.MiniAdHolder;
import com.water.alkaline.kengen.utils.Constant;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    public List<SaveEntity> arrayList = new ArrayList<>();
    OnVideoListener listener;
    HashMap<Integer, Integer> hashMap = new HashMap<>();

    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;

    public VideosAdapter(Activity activity, List<SaveEntity> arrayList, RecyclerView recyclerView, OnVideoListener listener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.listener = listener;

        if (!(activity instanceof PlayerActivity))
            setAds(false);

        if (recyclerView != null) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (!recyclerView.canScrollVertically(1)) {
                        if (!isLoading) {
                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            isLoading = true;
                        }
                    }
                }
            });
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemVideoBinding binding;

        public ViewHolder(ItemVideoBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public void setAds(boolean isAds) {
        int PARTICLE_AD_DISPLAY_COUNT = PowerPreference.getDefaultFile().getInt(AdUtils.ListNativeAfterCount, 10);
        if (PARTICLE_AD_DISPLAY_COUNT > 0) {

            arrayList.removeAll(Collections.singleton(null));
            ArrayList<SaveEntity> tempArr = new ArrayList<>();

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
        else if (arrayList.get(position).videoId.equalsIgnoreCase("99999"))
            return Constant.LOADING;
        else return Constant.STORE_TYPE;
    }


    public class LoadingView extends RecyclerView.ViewHolder {
        LayoutProgressBinding progressBinding;

        public LoadingView(@NonNull LayoutProgressBinding itemView) {
            super(itemView.getRoot());
            progressBinding = itemView;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Constant.STORE_TYPE)
            return new ViewHolder(ItemVideoBinding.inflate(LayoutInflater.from(activity), parent, false));
        else if (viewType == Constant.LOADING)
            return new LoadingView(LayoutProgressBinding.inflate(LayoutInflater.from(activity), parent, false));
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

    public void refreshAdapter(List<SaveEntity> arrayList) {
        this.arrayList = arrayList;
        isLoading = false;
        if (!(activity instanceof PlayerActivity))
            setAds(true);
        else notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LargeAdHolder) {
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
        } else if (holder instanceof LoadingView) {

        } else {
            ViewHolder viewHolder = (ViewHolder) holder;

            Glide.with(activity).load(arrayList.get(position).imgUrl)
                    .placeholder(MyApplication.getPlaceHolder())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.binding.imgVideo);
            viewHolder.binding.txtVideoTitle.setText(Html.fromHtml(arrayList.get(position).title));
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


