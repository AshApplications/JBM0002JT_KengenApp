package com.water.alkaline.kengen.ui.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.water.alkaline.kengen.databinding.AdsNativeMiniBinding;
import com.water.alkaline.kengen.databinding.NativeAdMiniBinding;

import org.jetbrains.annotations.NotNull;

public class MiniAdHolder extends RecyclerView.ViewHolder {
    public NativeAdMiniBinding binding;

    public MiniAdHolder(@NonNull @NotNull NativeAdMiniBinding itemView) {
        super(itemView.getRoot());
        this.binding = itemView;
    }
}
