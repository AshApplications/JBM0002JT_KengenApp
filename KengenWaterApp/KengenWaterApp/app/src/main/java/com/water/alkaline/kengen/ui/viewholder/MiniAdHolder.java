package com.water.alkaline.kengen.ui.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gms.ads.databinding.LayoutAdMiniBinding;

public class MiniAdHolder extends RecyclerView.ViewHolder {
    public LayoutAdMiniBinding binding;

    public MiniAdHolder(@NonNull LayoutAdMiniBinding itemView) {
        super(itemView.getRoot());
        this.binding = itemView;
    }
}
