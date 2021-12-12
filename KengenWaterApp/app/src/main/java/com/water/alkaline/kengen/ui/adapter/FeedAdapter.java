package com.water.alkaline.kengen.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.preference.PowerPreference;
import com.water.alkaline.kengen.databinding.AdLayoutNativeBinding;
import com.water.alkaline.kengen.databinding.ItemFeedbackBinding;
import com.water.alkaline.kengen.model.feedback.Feedback;
import com.water.alkaline.kengen.placements.NativeAds;
import com.water.alkaline.kengen.utils.Constant;

import org.jetbrains.annotations.NotNull;

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
        if (PowerPreference.getDefaultFile().getInt(Constant.QUREKA, 5) <= 5 && PowerPreference.getDefaultFile().getInt(Constant.NATIVE, 5) <= 0) {
            setAds();
        }
    }

    public void refresh(List<Feedback> feedbacks) {
        this.arrayList = feedbacks;
        if (PowerPreference.getDefaultFile().getInt(Constant.QUREKA, 5) <= 5 && PowerPreference.getDefaultFile().getInt(Constant.NATIVE, 5) <= 0) {
            setAds();
        } else {
            notifyDataSetChanged();
        }
    }

    public class FeedHolder extends RecyclerView.ViewHolder {
        ItemFeedbackBinding binding;

        public FeedHolder(@NonNull @NotNull ItemFeedbackBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public class AdHolder extends RecyclerView.ViewHolder {
        AdLayoutNativeBinding binding;

        public AdHolder(AdLayoutNativeBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }


    public void setAds() {
        int PARTICLE_AD_DISPLAY_COUNT = PowerPreference.getDefaultFile().getInt(Constant.AD_DISPLAY_COUNT, 10);
        arrayList.removeAll(Collections.singleton(null));
        ArrayList<Feedback> tempArr = new ArrayList<>();
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
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == Constant.AD_TYPE)
            return new AdHolder(AdLayoutNativeBinding.inflate(LayoutInflater.from(activity), parent, false));
        else
            return new FeedHolder(ItemFeedbackBinding.inflate(LayoutInflater.from(activity), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AdHolder) {
            if (!hashMap.containsKey(position)) {
                hashMap.put(position, position);
                new NativeAds().shownatives(activity, ((AdHolder) holder).binding.includedGoogle, ((AdHolder) holder).binding.includedAppo, ((AdHolder) holder).binding.adSpaceNative);
            }else {
                if (PowerPreference.getDefaultFile().getInt(Constant.QUREKA, 5) > 0) {
                    new NativeAds().shownatives(activity, ((AdHolder) holder).binding.includedGoogle, ((AdHolder) holder).binding.includedAppo, ((AdHolder) holder).binding.adSpaceNative);
                }
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
