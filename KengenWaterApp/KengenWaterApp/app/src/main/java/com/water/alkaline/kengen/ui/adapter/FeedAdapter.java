package com.water.alkaline.kengen.ui.adapter;

import android.app.Activity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gms.ads.databinding.LayoutAdLargeBinding;
import com.google.gms.ads.databinding.LayoutAdMediumBinding;
import com.google.gms.ads.databinding.LayoutAdMiniBinding;
import com.preference.PowerPreference;
import com.google.gms.ads.AdUtils;
import com.water.alkaline.kengen.databinding.ItemFeedbackBinding;
import com.water.alkaline.kengen.model.feedback.Feedback;
import com.google.gms.ads.MainAds;
import com.water.alkaline.kengen.model.main.Banner;
import com.water.alkaline.kengen.ui.viewholder.LargeAdHolder;
import com.water.alkaline.kengen.ui.viewholder.MediumAdHolder;
import com.water.alkaline.kengen.ui.viewholder.MiniAdHolder;
import com.water.alkaline.kengen.utils.Constant;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    public List<Feedback> arrayList = new ArrayList<>();

    HashMap<Integer, Integer> hashMap = new HashMap<>();

    public FeedAdapter(Activity activity, List<Feedback> feedbacks) {
        this.activity = activity;
        this.arrayList = feedbacks;
        setAds(false);
    }

    public void refresh(List<Feedback> feedbacks) {
        this.arrayList = feedbacks;
        setAds(true);
    }

    public class FeedHolder extends RecyclerView.ViewHolder {
        ItemFeedbackBinding binding;

        public FeedHolder(@NonNull ItemFeedbackBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public void setAds(boolean isAds) {
        int PARTICLE_AD_DISPLAY_COUNT = PowerPreference.getDefaultFile().getInt(AdUtils.ListNativeAfterCount, 10);
        if (PARTICLE_AD_DISPLAY_COUNT > 0) {

            arrayList.removeAll(Collections.singleton(null));
            ArrayList<Feedback> tempArr = new ArrayList<>();

            if (arrayList.size() > 0) {
                tempArr.add(null);
            }

            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.size() > PARTICLE_AD_DISPLAY_COUNT) {
                    if (i != 0 && i % PARTICLE_AD_DISPLAY_COUNT == 0) {
                        tempArr.add(null);
                    }
                    tempArr.add(arrayList.get(i));
                } else {
                    tempArr.add(arrayList.get(i));
                }
            }

            this.arrayList = tempArr;
        }

        if (isAds)
            notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayList.get(position) == null)
            return Constant.AD_TYPE;
        else return Constant.STORE_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Constant.STORE_TYPE)
            return new FeedHolder(ItemFeedbackBinding.inflate(LayoutInflater.from(activity), parent, false));
        else {
            if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneListNative, 0) == 1)
                return new LargeAdHolder(LayoutAdLargeBinding.inflate(LayoutInflater.from(activity), parent, false));
            else if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneListNative, 0) == 2)
                return new MiniAdHolder(LayoutAdMiniBinding.inflate(LayoutInflater.from(activity), parent, false));
            else if (PowerPreference.getDefaultFile().getInt(AdUtils.WhichOneListNative, 0) == 3)
                return new MediumAdHolder(LayoutAdMediumBinding.inflate(LayoutInflater.from(activity), parent, false));
            else
                return new MiniAdHolder(LayoutAdMiniBinding.inflate(LayoutInflater.from(activity), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FeedHolder) {
            FeedHolder holder1 = (FeedHolder) holder;
            Feedback feedback = arrayList.get(position);
            holder1.binding.txtId.setText("ID : " + feedback.id);
            holder1.binding.txtVideoDatetime.setText(feedback.feedtime);

            holder1.binding.ratingBar.setRating(Float.parseFloat(feedback.star));
            holder1.binding.txtQuestion.setText(feedback.message);

            if (feedback.reply.equalsIgnoreCase(""))
                holder1.binding.txtAnswer.setText("No Reply Found.");
            else holder1.binding.txtAnswer.setText(feedback.reply);

        } else if (holder instanceof LargeAdHolder) {
            LargeAdHolder adHolder = (LargeAdHolder) holder;
            new MainAds().showListNativeAds(activity, adHolder.binding.adFrameLarge, adHolder.binding.adSpaceLarge);
            int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics());
            ((LargeAdHolder) holder).binding.adFrameLarge.setPadding(0, dp, dp, 0);
        } else if (holder instanceof MiniAdHolder) {
            MiniAdHolder adHolder = (MiniAdHolder) holder;
            new MainAds().showListNativeAds(activity, adHolder.binding.adFrameLarge, adHolder.binding.adSpaceLarge);
            int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics());
            ((MiniAdHolder) holder).binding.adFrameLarge.setPadding(0, dp, dp, 0);
        } else if (holder instanceof MediumAdHolder) {
            MediumAdHolder adHolder = (MediumAdHolder) holder;
            new MainAds().showListNativeAds(activity, adHolder.binding.adFrameLarge, adHolder.binding.adSpaceLarge);
            int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics());
            ((MediumAdHolder) holder).binding.adFrameLarge.setPadding(0, dp, dp, 0);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
