package com.water.alkaline.kengen.ui.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.water.alkaline.kengen.databinding.NativeAdLargeBinding;
import com.water.alkaline.kengen.databinding.NativeLayoutLargeBinding;


public class LargeAdHolder extends RecyclerView.ViewHolder {
    public NativeLayoutLargeBinding binding;

    public LargeAdHolder(@NonNull NativeLayoutLargeBinding itemView) {
        super(itemView.getRoot());
        this.binding = itemView;
    }
}
