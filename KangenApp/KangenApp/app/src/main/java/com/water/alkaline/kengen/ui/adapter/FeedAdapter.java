package com.water.alkaline.kengen.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.databinding.ItemFeedbackBinding;
import com.water.alkaline.kengen.library.ViewAnimator.ViewAnimator;
import com.water.alkaline.kengen.model.feedback.Feedback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    public List<Feedback> feedbacks = new ArrayList<>();


    public FeedAdapter(Activity activity, List<Feedback> feedbacks) {
        this.activity = activity;
        this.feedbacks = feedbacks;
    }

    public void refresh(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
        notifyDataSetChanged();
    }

    public class FeedHolder extends RecyclerView.ViewHolder {
        ItemFeedbackBinding binding;

        public FeedHolder(@NonNull @NotNull ItemFeedbackBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new FeedHolder(ItemFeedbackBinding.inflate(LayoutInflater.from(activity), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        FeedHolder holder1 = (FeedHolder) holder;
        Feedback feedback = feedbacks.get(position);
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
        return feedbacks.size();
    }
}
