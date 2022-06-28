package com.water.alkaline.kengen.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.Encrypt.DecryptEncrypt;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.network.RetroClient;
import com.water.alkaline.kengen.databinding.DialogInternetBinding;
import com.water.alkaline.kengen.databinding.DialogLoadingBinding;
import com.water.alkaline.kengen.databinding.FragmentFeedbackBinding;
import com.water.alkaline.kengen.library.ViewAnimator.AnimationListener;
import com.water.alkaline.kengen.library.ViewAnimator.ViewAnimator;
import com.water.alkaline.kengen.model.feedback.FeedbackResponse;
import com.water.alkaline.kengen.model.update.UpdateResponse;
import com.water.alkaline.kengen.ui.activity.FeedbackActivity;
import com.water.alkaline.kengen.utils.Constant;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackFragment extends Fragment implements RatingBar.OnRatingBarChangeListener {

    FragmentFeedbackBinding binding;
    FeedbackActivity activity;
    boolean isAnimated = false;

    public Dialog dialog;
    public Dialog loaderDialog;

    public FeedbackFragment() {
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        if (activity == null) {
            activity = (FeedbackActivity) context;
        }
    }

    public void dismiss_loader_dialog() {
        if (loaderDialog != null && loaderDialog.isShowing())
            loaderDialog.dismiss();
    }

    public void loader_dialog() {
        loaderDialog = new Dialog(activity, R.style.NormalDialog);
        DialogLoadingBinding loadingBinding = DialogLoadingBinding.inflate(getLayoutInflater());
        loaderDialog.setContentView(loadingBinding.getRoot());
        Objects.requireNonNull(loaderDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loaderDialog.setCancelable(false);
        loaderDialog.setCanceledOnTouchOutside(false);
        loaderDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loaderDialog.show();
    }


    public void dismiss_dialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public DialogInternetBinding network_dialog(String text) {
        dialog = new Dialog(activity, R.style.NormalDialog);
        DialogInternetBinding binding = DialogInternetBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        binding.txtError.setText(text);
        return binding;
    }


    public FeedbackFragment(FeedbackActivity activity) {
        this.activity = activity;

    }

    public static FeedbackFragment newInstance(FeedbackActivity activity) {
        return new FeedbackFragment(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFeedbackBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (activity != null) {
            disableEmojiInTitle();
            initializeUI();
        }
    }

    private void disableEmojiInTitle() {
        InputFilter emojiFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int index = start; index < end - 1; index++) {
                    int type = Character.getType(source.charAt(index));

                    if (type == Character.SURROGATE) {
                        return "";
                    }
                }
                return null;
            }
        };
        binding.txtComments.setFilters(new InputFilter[]{emojiFilter});
    }

    public void initializeUI() {
        binding.ratingBar.setRating(0);
        binding.layoutForm.setVisibility(View.INVISIBLE);

        binding.ratingBar.setOnRatingBarChangeListener(this);

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClick();
            }
        });
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float value, boolean b) {
        if (!isAnimated) {
            binding.layoutForm.setVisibility(View.VISIBLE);

            ViewAnimator
                    .animate(binding.lblHowHappy)
                    .dp().translationY(0, -75)
                    .duration(350)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .andAnimate(ratingBar)
                    .dp().translationY(0, -75)
                    .duration(450)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .andAnimate(binding.layoutForm)
                    .dp().translationY(0, -75)
                    .singleInterpolator(new LinearOutSlowInInterpolator())
                    .duration(450)
                    .alpha(0, 1)
                    .interpolator(new FastOutSlowInInterpolator())
                    .andAnimate(binding.lblWeHearFeedback)
                    .dp().translationY(0, -20)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .duration(300)
                    .alpha(0, 1)
                    .andAnimate(binding.txtComments)
                    .dp().translationY(30, -30)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .duration(550)
                    .alpha(0, 1)
                    .andAnimate(binding.btnSubmit)
                    .dp().translationY(60, -35)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .duration(800)
                    .alpha(0, 1)
                    .onStop(new AnimationListener.Stop() {
                        @Override
                        public void onStop() {
                            isAnimated = true;
                        }
                    })
                    .start();
        }
    }

    public void onSubmitClick() {
        if (binding.txtComments.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(activity, "Plz Enter Something", Toast.LENGTH_SHORT).show();
        } else {
            sendFeedback();
        }
    }


    public void updateError() {
        network_dialog(getResources().getString(R.string.error_internet)).txtRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss_dialog();
                if (Constant.checkInternet(activity)) {
                    sendFeedback();
                } else dialog.show();
            }
        });
    }

    public void sendFeedback() {
        if (Constant.checkInternet(activity)) {
            loader_dialog();
            @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
            RetroClient.getInstance().getApi().SendfeedApi(deviceId, String.valueOf(binding.ratingBar.getRating()), binding.txtComments.getText().toString())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            dismiss_loader_dialog();

                            if (response.isSuccessful()) {
                                try {
                                    final FeedbackResponse response1 = new GsonBuilder().create().fromJson((DecryptEncrypt.DecryptStr(response.body())), FeedbackResponse.class);
                                    if (response1 != null && response1.feedbacks != null)
                                        PowerPreference.getDefaultFile().putString(Constant.mFeeds, new Gson().toJson(response1.feedbacks));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            Toast.makeText(activity, "feedback submitted", Toast.LENGTH_SHORT).show();
                            animate();
                            activity.refresh();

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            dismiss_loader_dialog();
                            Toast.makeText(activity, "feedback submitted", Toast.LENGTH_SHORT).show();
                            animate();
                        }
                    });
        } else {
            updateError();
        }
    }

    public void animate() {
        binding.txtComments.setText("");
    }


}