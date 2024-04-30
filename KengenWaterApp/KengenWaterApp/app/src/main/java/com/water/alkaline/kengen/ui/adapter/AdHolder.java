package com.water.alkaline.kengen.ui.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gms.ads.databinding.LayoutAdUniversalBinding;

public class AdHolder extends RecyclerView.ViewHolder {

    LayoutAdUniversalBinding binding;

    public AdHolder(@NonNull LayoutAdUniversalBinding itemView) {
        super(itemView.getRoot());
        this.binding = itemView;
    }
}
