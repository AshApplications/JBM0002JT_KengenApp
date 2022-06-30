package com.water.alkaline.kengen.ui.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.water.alkaline.kengen.databinding.AdsNativeLargeBinding;
import com.water.alkaline.kengen.databinding.NativeAdLargeBinding;



public class LargeAdHolder extends RecyclerView.ViewHolder {
    public NativeAdLargeBinding binding;

    public LargeAdHolder(@NonNull NativeAdLargeBinding itemView) {
        super(itemView.getRoot());
        this.binding = itemView;
    }
}
