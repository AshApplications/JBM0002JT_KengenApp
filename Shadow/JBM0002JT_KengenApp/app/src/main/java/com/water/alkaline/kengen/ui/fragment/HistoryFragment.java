package com.water.alkaline.kengen.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.databinding.FragmentHistoryBinding;
import com.water.alkaline.kengen.library.ItemOffsetDecoration;
import com.water.alkaline.kengen.model.feedback.Feedback;
import com.water.alkaline.kengen.ui.activity.FeedbackActivity;
import com.water.alkaline.kengen.ui.adapter.FeedAdapter;
import com.water.alkaline.kengen.utils.Constant;



import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {


    FragmentHistoryBinding binding;
    Activity activity;
    List<Feedback> feedbacks = new ArrayList<>();
    FeedAdapter adapter;

    public HistoryFragment(Activity activity) {
        this.activity = activity;
    }


    public HistoryFragment() {
    }

    public static HistoryFragment newInstance(Activity activity) {
        return new HistoryFragment(activity);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (activity == null) {
            activity = (FeedbackActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (activity != null) {
            GridLayoutManager manager = new GridLayoutManager(activity, 1);
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int i) {
                    switch (adapter.getItemViewType(i)) {
                        case Constant.STORE_TYPE:
                            return 1;
                        case Constant.AD_TYPE:
                            return 1;
                        case Constant.LOADING:
                            return 1;
                        default:
                            return 1;

                    }
                }
            });
            binding.rvFeeds.setLayoutManager(manager);
            adapter = new FeedAdapter(activity, feedbacks);
            binding.rvFeeds.setAdapter(adapter);
            binding.rvFeeds.setItemViewCacheSize(100);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshActivity();
                }
            }, 500);
        }
    }

    public void refreshActivity() {
        try {
            Type type = new TypeToken<List<Feedback>>() {
            }.getType();

            feedbacks = new Gson().fromJson(PowerPreference.getDefaultFile().getString(Constant.mFeeds, new Gson().toJson(new ArrayList<Feedback>())), type);
        } catch (Exception e) {
            feedbacks = new ArrayList<>();
        }
        adapter.refresh(feedbacks);
        binding.includedProgress.progress.setVisibility(View.GONE);
        checkData();
    }

    public void checkData() {
        if (binding.rvFeeds.getAdapter() != null && binding.rvFeeds.getAdapter().getItemCount() > 0) {
            binding.includedProgress.llError.setVisibility(View.GONE);
        } else {
            binding.includedProgress.llError.setVisibility(View.VISIBLE);
        }
    }

}