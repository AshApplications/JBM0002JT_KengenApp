package com.water.alkaline.kengen.ui.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gms.ads.databinding.LayoutAdMediumBinding;


public class MediumAdHolder  extends RecyclerView.ViewHolder {

    public LayoutAdMediumBinding binding;

    public MediumAdHolder(@NonNull LayoutAdMediumBinding itemView) {
        super(itemView.getRoot());
        this.binding = itemView;
    }
}
