package com.water.alkaline.kengen.ui.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.water.alkaline.kengen.databinding.NativeAdMediumBinding;
import com.water.alkaline.kengen.databinding.NativeAdMiniBinding;
import com.water.alkaline.kengen.databinding.NativeLayoutMediumBinding;


public class MediumAdHolder extends RecyclerView.ViewHolder {
    public NativeLayoutMediumBinding binding;

    public MediumAdHolder(@NonNull NativeLayoutMediumBinding itemView) {
        super(itemView.getRoot());
        this.binding = itemView;
    }
}
