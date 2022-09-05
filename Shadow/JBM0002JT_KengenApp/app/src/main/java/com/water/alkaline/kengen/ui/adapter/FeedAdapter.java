package com.water.alkaline.kengen.ui.adapter;

import android.app.Activity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.preference.PowerPreference;
import com.water.alkaline.kengen.databinding.ItemFeedbackBinding;
import com.water.alkaline.kengen.databinding.ItemImageBinding;
import com.water.alkaline.kengen.databinding.NativeAdLargeBinding;
import com.water.alkaline.kengen.databinding.NativeAdMediumBinding;
import com.water.alkaline.kengen.databinding.NativeAdMiniBinding;
import com.water.alkaline.kengen.model.feedback.Feedback;
import com.water.alkaline.kengen.placements.ListNativeAds;
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
        setAds();
    }

    public void refresh(List<Feedback> feedbacks) {
        this.arrayList = feedbacks;
        setAds();
    }

    public class FeedHolder extends RecyclerView.ViewHolder {
        ItemFeedbackBinding binding;

        public FeedHolder(@NonNull ItemFeedbackBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public void setAds() {
        int PARTICLE_AD_DISPLAY_COUNT = PowerPreference.getDefaultFile().getInt(Constant.ListNativeAfterCount, 10);
        arrayList.removeAll(Collections.singleton(null));
        ArrayList<Feedback> tempArr = new ArrayList<>();
        if (arrayList.size() > 0) {
            tempArr.add(null);
        }
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.size() > PARTICLE_AD_DISPLAY_COUNT) {
                if (i != 0) {
                    if (i % PARTICLE_AD_DISPLAY_COUNT == 0) {
                        tempArr.add(null);
                    }
                }
                tempArr.add(arrayList.get(i));
            } else {
                tempArr.add(arrayList.get(i));
            }
        }
        if (arrayList.size() > 0) {
            if (arrayList.size() % PARTICLE_AD_DISPLAY_COUNT == 0) {
                tempArr.add(null);
            }
        }

        this.arrayList = tempArr;
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
            if (PowerPreference.getDefaultFile().getInt(Constant.ListNativeWhichOne, 0) == 0)
                return new LargeAdHolder(NativeAdLargeBinding.inflate(LayoutInflater.from(activity), parent, false));
            else
                return new MediumAdHolder(NativeAdMediumBinding.inflate(LayoutInflater.from(activity), parent, false));
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
            new ListNativeAds().showListNativeAds(activity, adHolder.binding.frameNativeLarge, adHolder.binding.adSpaceLarge);
            int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics());
            ((LargeAdHolder) holder).binding.adNative.setPadding(0, dp, dp, 0);
        } else if (holder instanceof MediumAdHolder) {
            MediumAdHolder adHolder = (MediumAdHolder) holder;
            new ListNativeAds().showListNativeAds(activity, adHolder.binding.frameNativeMedium, adHolder.binding.adSpaceMedium);
            int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics());
            ((MediumAdHolder) holder).binding.adNative.setPadding(0, dp, dp, 0);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
