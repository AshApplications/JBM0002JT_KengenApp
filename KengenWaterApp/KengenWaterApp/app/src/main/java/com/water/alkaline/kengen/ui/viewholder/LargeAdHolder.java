package com.water.alkaline.kengen.ui.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gms.ads.databinding.LayoutAdLargeBinding;


public class LargeAdHolder extends RecyclerView.ViewHolder {
    public LayoutAdLargeBinding binding;

    public LargeAdHolder(@NonNull LayoutAdLargeBinding itemView) {
        super(itemView.getRoot());
        this.binding = itemView;
    }
}
