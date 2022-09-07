package com.water.alkaline.kengen.ui.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.water.alkaline.kengen.databinding.NativeAdMiniBinding;
import com.water.alkaline.kengen.databinding.NativeLayoutMiniBinding;


public class MiniAdHolder extends RecyclerView.ViewHolder {
    public NativeLayoutMiniBinding binding;

    public MiniAdHolder(@NonNull NativeLayoutMiniBinding itemView) {
        super(itemView.getRoot());
        this.binding = itemView;
    }
}
