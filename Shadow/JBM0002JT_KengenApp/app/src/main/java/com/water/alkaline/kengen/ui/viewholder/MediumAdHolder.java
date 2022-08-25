package com.water.alkaline.kengen.ui.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.water.alkaline.kengen.databinding.NativeAdMediumBinding;
import com.water.alkaline.kengen.databinding.NativeAdMiniBinding;



public class MediumAdHolder extends RecyclerView.ViewHolder {
    public NativeAdMediumBinding binding;

    public MediumAdHolder(@NonNull NativeAdMediumBinding itemView) {
        super(itemView.getRoot());
        this.binding = itemView;
    }
}
