package com.water.alkaline.kengen.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.water.alkaline.kengen.databinding.ItemDrawerCategoryBinding;
import com.water.alkaline.kengen.model.main.Category;
import com.water.alkaline.kengen.ui.listener.OnDrawerCatListener;

import java.util.ArrayList;
import java.util.List;

public class DrawerCatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    List<Category> arrayList = new ArrayList<>();
    OnDrawerCatListener listener;

    public DrawerCatAdapter(Activity activity, List<Category> arrayList, OnDrawerCatListener listener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemDrawerCategoryBinding binding;

        public ViewHolder(ItemDrawerCategoryBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemDrawerCategoryBinding.inflate(LayoutInflater.from(activity), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.binding.txtCategory.setText(arrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
