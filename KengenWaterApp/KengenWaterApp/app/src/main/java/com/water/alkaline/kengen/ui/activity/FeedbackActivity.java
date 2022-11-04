package com.water.alkaline.kengen.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.Encrypt.DecryptEncrypt;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.network.RetroClient;
import com.water.alkaline.kengen.databinding.ActivityFeedbackBinding;
import com.water.alkaline.kengen.databinding.DialogInternetBinding;
import com.water.alkaline.kengen.databinding.DialogLoadingBinding;
import com.water.alkaline.kengen.model.feedback.FeedbackResponse;
import com.google.gms.ads.BackInterAds;
import com.google.gms.ads.MainAds;
import com.water.alkaline.kengen.ui.adapter.ViewPagerFragmentAdapter;
import com.water.alkaline.kengen.ui.fragment.FeedbackFragment;
import com.water.alkaline.kengen.ui.fragment.HistoryFragment;
import com.water.alkaline.kengen.utils.Constant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends AppCompatActivity {

    ActivityFeedbackBinding binding;
    ViewPagerFragmentAdapter adapter;

    public Dialog dialog;
    public Dialog loaderDialog;

    @Override
    protected void onResume() {
        super.onResume();
        new MainAds().showBannerAds(this, binding.includedAd.adFrameMini, binding.includedAd.adSpaceMini);
    }

    @Override
    public void onBackPressed() {
        new MainAds().showBackInterAds(this, this::finish);
    }

    public void setBG() {

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setBG();
        startFeed();
    }

    public void startFeed() {
        adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), getLifecycle());
        adapter.addFragment(FeedbackFragment.newInstance(this), "Feedbacks");
        adapter.addFragment(HistoryFragment.newInstance(this), "History");
        binding.vpFeeds.setAdapter(adapter);
        binding.vpFeeds.setOffscreenPageLimit(2);

        new TabLayoutMediator(binding.tabHome, binding.vpFeeds, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(adapter.mFragmentTitleList.get(position));
            }
        }).attach();

        binding.vpFeeds.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (adapter.arrayList.get(position) instanceof HistoryFragment) {
                    binding.ivInfo.setVisibility(View.VISIBLE);
                } else {
                    binding.ivInfo.setVisibility(View.GONE);
                }
            }
        });

        binding.ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedAPI();
            }
        });
    }


    public void dismiss_loader_dialog() {
        if (loaderDialog != null && loaderDialog.isShowing())
            loaderDialog.dismiss();
    }

    public void loader_dialog() {
        loaderDialog = new Dialog(this, R.style.NormalDialog);
        DialogLoadingBinding loadingBinding = DialogLoadingBinding.inflate(getLayoutInflater());
        loaderDialog.setContentView(loadingBinding.getRoot());
        loaderDialog.setCancelable(false);
        loaderDialog.setCanceledOnTouchOutside(false);
        loaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loaderDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loaderDialog.show();
    }

    public void dismiss_dialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public DialogInternetBinding network_dialog(String text) {
        dialog = new Dialog(this, R.style.NormalDialog);
        DialogInternetBinding binding = DialogInternetBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        binding.txtError.setText(text);
        return binding;
    }


    public void FeedError() {
        network_dialog(getResources().getString(R.string.error_internet)).txtRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss_dialog();
                if (Constant.checkInternet(FeedbackActivity.this)) {
                    FeedAPI();
                } else dialog.show();
            }
        });
    }

    public void FeedAPI() {
        if (Constant.checkInternet(FeedbackActivity.this)) {
            loader_dialog();
            RetroClient.getInstance(this).getApi().GetfeedApi(DecryptEncrypt.EncryptStr(FeedbackActivity.this, PowerPreference.getDefaultFile().getString(Constant.mToken, "123")))
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            dismiss_loader_dialog();
                            try {
                                final FeedbackResponse response1 = new GsonBuilder().create().fromJson((DecryptEncrypt.DecryptStr(FeedbackActivity.this, response.body())), FeedbackResponse.class);

                                if (response1 != null && response1.feedbacks != null)
                                    PowerPreference.getDefaultFile().putString(Constant.mFeeds, new Gson().toJson(response1.feedbacks));

                            } catch (Exception e) {
                                Constant.showLog(e.toString());
                                e.printStackTrace();
                                Constant.showToast(FeedbackActivity.this, "Something went Wrong");
                            }

                            ((HistoryFragment) adapter.arrayList.get(1)).refreshActivity();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            dismiss_loader_dialog();
                            FeedError();
                        }
                    });
        } else {
            FeedError();
        }
    }

    public void refresh() {
        ((HistoryFragment) adapter.arrayList.get(1)).refreshActivity();
    }


}