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
import com.water.alkaline.kengen.databinding.AdLayoutNativeBinding;
import com.water.alkaline.kengen.databinding.ItemImageBinding;
import com.water.alkaline.kengen.model.main.Subcategory;
import com.water.alkaline.kengen.ui.listener.OnSubcatListener;
import com.water.alkaline.kengen.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class SubcatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    List<Subcategory> arrayList = new ArrayList<>();
    OnSubcatListener listener;


    public SubcatAdapter(Activity activity, List<Subcategory> arrayList, OnSubcatListener listener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemImageBinding binding;

        public ViewHolder(ItemImageBinding itemView) {
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
            return new ViewHolder(ItemImageBinding.inflate(LayoutInflater.from(activity), parent, false));
    }

    public void refreshAdapter(List<Subcategory> arrayList) {
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
        viewHolder.binding.txtVideoTitle.setSelected(true);
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
