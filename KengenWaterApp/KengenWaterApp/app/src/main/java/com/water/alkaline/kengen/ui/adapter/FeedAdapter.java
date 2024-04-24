package com.water.alkaline.kengen.ui.adapter;

import android.app.Activity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.preference.PowerPreference;
import com.google.gms.ads.AdUtils;
import com.water.alkaline.kengen.databinding.ItemFeedbackBinding;
import com.water.alkaline.kengen.model.feedback.Feedback;
import com.water.alkaline.kengen.model.main.Banner;
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
    }

    public void refresh(List<Feedback> feedbacks) {
        this.arrayList = feedbacks;
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
        return Constant.STORE_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FeedHolder(ItemFeedbackBinding.inflate(LayoutInflater.from(activity), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
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

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
