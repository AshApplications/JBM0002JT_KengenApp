package com.water.alkaline.kengen.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.databinding.AdLayoutNativeBinding;
import com.water.alkaline.kengen.databinding.ItemVideoBinding;
import com.water.alkaline.kengen.library.ViewAnimator.ViewAnimator;
import com.water.alkaline.kengen.model.DownloadEntity;
import com.water.alkaline.kengen.model.feedback.Feedback;
import com.water.alkaline.kengen.model.main.Pdf;
import com.water.alkaline.kengen.placements.NativeListAds;
import com.water.alkaline.kengen.ui.listener.OnPdfListener;
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
        if (PowerPreference.getDefaultFile().getInt(Constant.QUREKA, 5) <= 0 && PowerPreference.getDefaultFile().getInt(Constant.NATIVE, 5) <= 0) {
            setAds();
        }
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
        ArrayList<Pdf> tempArr = new ArrayList<>();
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
        if (viewType == Constant.AD_TYPE)
            return new AdHolder(AdLayoutNativeBinding.inflate(LayoutInflater.from(activity), parent, false));
        else
            return new ViewHolder(ItemVideoBinding.inflate(LayoutInflater.from(activity), parent, false));
    }

    public void refreshAdapter(List<Pdf> arrayList) {
        this.arrayList = arrayList;
        if (PowerPreference.getDefaultFile().getInt(Constant.QUREKA, 5) <= 0 && PowerPreference.getDefaultFile().getInt(Constant.NATIVE, 5) <= 0) {
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
                new NativeListAds().shownatives(activity, ((AdHolder) holder).binding.includedGoogle, ((AdHolder) holder).binding.includedAppo, ((AdHolder) holder).binding.adSpaceNative);
            }else {
                if (PowerPreference.getDefaultFile().getInt(Constant.QUREKA, 5) > 0) {
                    new NativeListAds().shownatives(activity, ((AdHolder) holder).binding.includedGoogle, ((AdHolder) holder).binding.includedAppo, ((AdHolder) holder).binding.adSpaceNative);
                }
            }
        } else {
            ViewHolder viewHolder = (ViewHolder) holder;
            Glide.with(activity).load(arrayList.get(position).getImage())
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
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
