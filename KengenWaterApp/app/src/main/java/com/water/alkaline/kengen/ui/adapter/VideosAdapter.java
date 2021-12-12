package com.water.alkaline.kengen.ui.adapter;


import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.databinding.AdLayoutNativeBinding;
import com.water.alkaline.kengen.databinding.ItemVideoBinding;
import com.water.alkaline.kengen.databinding.LayoutProgressBinding;
import com.water.alkaline.kengen.model.SaveEntity;
import com.water.alkaline.kengen.placements.NativeAds;
import com.water.alkaline.kengen.ui.activity.PlayerActivity;
import com.water.alkaline.kengen.ui.listener.OnLoadMoreListener;
import com.water.alkaline.kengen.ui.listener.OnVideoListener;
import com.water.alkaline.kengen.utils.Constant;

import org.jetbrains.annotations.NotNull;

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

        if (PowerPreference.getDefaultFile().getInt(Constant.QUREKA, 5) <= 5 && PowerPreference.getDefaultFile().getInt(Constant.NATIVE, 5) <= 0 && !(activity instanceof PlayerActivity)) {
            setAds();
        }

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


    public class AdHolder extends RecyclerView.ViewHolder {
        AdLayoutNativeBinding binding;

        public AdHolder(AdLayoutNativeBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public void setAds() {
        int PARTICLE_AD_DISPLAY_COUNT = PowerPreference.getDefaultFile().getInt(Constant.AD_DISPLAY_COUNT, 10);
        arrayList.removeAll(Collections.singleton(null));
        ArrayList<SaveEntity> tempArr = new ArrayList<>();
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
        else if (arrayList.get(position).videoId.equalsIgnoreCase("99999"))
            return Constant.LOADING;
        else return Constant.STORE_TYPE;
    }


    public class LoadingView extends RecyclerView.ViewHolder {
        LayoutProgressBinding progressBinding;

        public LoadingView(@NonNull @NotNull LayoutProgressBinding itemView) {
            super(itemView.getRoot());
            progressBinding = itemView;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case Constant.AD_TYPE:
                return new AdHolder(AdLayoutNativeBinding.inflate(LayoutInflater.from(activity), parent, false));
            case Constant.LOADING:
                return new LoadingView(LayoutProgressBinding.inflate(LayoutInflater.from(activity), parent, false));
            case Constant.STORE_TYPE:
                return new ViewHolder(ItemVideoBinding.inflate(LayoutInflater.from(activity), parent, false));
        }
        return null;
    }

    public void refreshAdapter(List<SaveEntity> arrayList) {
        this.arrayList = arrayList;
        isLoading = false;
        if (PowerPreference.getDefaultFile().getInt(Constant.QUREKA, 5) <= 5 && PowerPreference.getDefaultFile().getInt(Constant.NATIVE, 5) <= 0 && !(activity instanceof PlayerActivity)) {
            setAds();
        } else {
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AdHolder) {
            if (!hashMap.containsKey(position)) {
                hashMap.put(position, position);
                new NativeAds().shownatives(activity, ((AdHolder) holder).binding.includedGoogle, ((AdHolder) holder).binding.includedAppo, ((AdHolder) holder).binding.adSpaceNative);
            } else {
                if (PowerPreference.getDefaultFile().getInt(Constant.QUREKA, 5) > 0) {
                    new NativeAds().shownatives(activity, ((AdHolder) holder).binding.includedGoogle, ((AdHolder) holder).binding.includedAppo, ((AdHolder) holder).binding.adSpaceNative);
                }
            }
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


