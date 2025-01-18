package com.water.alkaline.kengen.ui.feedback.fragment;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gms.ads.AdLoader;
import com.google.gms.ads.MyApp;
import com.google.gms.ads.databinding.LayoutAdUniversalBinding;
import com.preference.PowerPreference;
import com.google.gms.ads.AdUtils;
import com.water.alkaline.kengen.databinding.ItemFeedbackBinding;
import com.water.alkaline.kengen.model.feedback.Feedback;
import com.water.alkaline.kengen.model.main.Banner;
import com.water.alkaline.kengen.model.main.Channel;
import com.water.alkaline.kengen.ui.adapter.AdHolder;
import com.water.alkaline.kengen.utils.Constant;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context activity;
    public List<Feedback> arrayList = new ArrayList<>();

    HashMap<Integer, Integer> hashMap = new HashMap<>();

    public FeedAdapter(Context activity, List<Feedback> feedbacks) {
        this.activity = activity;
        this.arrayList = feedbacks;
        setAds(false);
    }

    public void refresh(List<Feedback> feedbacks) {
        this.arrayList = feedbacks;
        setAds(true);
    }

    public void addItem(Feedback feedback) {
        this.arrayList.add(0,feedback);
        setAds(true);
    }

    public void setAds(boolean isAds) {
        int PARTICLE_AD_DISPLAY_COUNT = MyApp.getAdModel().getAdsListViewCount();

        if (PARTICLE_AD_DISPLAY_COUNT > 0 && MyApp.getAdModel().getAdsOnOff().equalsIgnoreCase("Yes")) {

            arrayList.removeAll(Collections.singleton(null));
            ArrayList<Feedback> tempArr = new ArrayList<>();

            if (!arrayList.isEmpty()) {
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

    public class FeedHolder extends RecyclerView.ViewHolder {
        ItemFeedbackBinding binding;

        public FeedHolder(@NonNull ItemFeedbackBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return arrayList.get(position) == null ? Constant.AD_TYPE : Constant.STORE_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Constant.AD_TYPE)
            return new AdHolder(LayoutAdUniversalBinding.inflate(LayoutInflater.from(activity), parent, false));
        else
            return new FeedHolder(ItemFeedbackBinding.inflate(LayoutInflater.from(activity), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AdHolder) {
            AdHolder adHolder = (AdHolder) holder;
            if (adHolder.binding.flAd.getChildCount() <= 0) {
                AdLoader.getInstance().showNativeList(activity, adHolder.binding);
            }
        } else {
            FeedHolder holder1 = (FeedHolder) holder;
            Feedback feedback = arrayList.get(position);
            holder1.binding.txtId.setText("ID : " + feedback.id);
            holder1.binding.txtVideoDatetime.setText(feedback.feedtime);

            holder1.binding.ratingBar.setRating(Float.parseFloat(feedback.star));
            holder1.binding.txtQuestion.setText(feedback.message);

            if (feedback.reply.equalsIgnoreCase(""))
                holder1.binding.txtAnswer.setText("No Reply Found.");
            else holder1.binding.txtAnswer.setText(feedback.reply);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
