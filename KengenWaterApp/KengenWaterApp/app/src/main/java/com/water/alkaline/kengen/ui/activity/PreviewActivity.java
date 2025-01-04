package com.water.alkaline.kengen.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gms.ads.AdLoader;
import com.google.gms.ads.MyApp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.databinding.ActivityPreviewBinding;
import com.water.alkaline.kengen.databinding.DialogLoadingBinding;
import com.water.alkaline.kengen.library.ActionListeners;
import com.water.alkaline.kengen.library.ViewToImage;
import com.water.alkaline.kengen.model.SaveEntity;
import com.water.alkaline.kengen.utils.Constant;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.utils.uiController;


import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PreviewActivity extends AppCompatActivity {

    ActivityPreviewBinding binding;
    ArrayList<SaveEntity> mList = new ArrayList<>();
    int pos = 0;

    Dialog loaderDialog;

    public void dismiss_loader_dialog() {
        if (loaderDialog != null && loaderDialog.isShowing())
            loaderDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        uiController.onBackPressed(this);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApp.getAdModel().getAdsOnOff().equalsIgnoreCase("Yes")) {
            if (binding.includedAd.flAd.getChildCount() <= 0) {
                AdLoader.getInstance().showNativeLarge(this, binding.includedAd);
            }
        } else {
            binding.includedAd.cvAdMain.setVisibility(View.GONE);
            binding.includedAd.flAd.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent() != null && getIntent().hasExtra(Constant.POSITION)) {
            pos = getIntent().getIntExtra(Constant.POSITION, 0);
        }

        try {
            Type type = new TypeToken<List<SaveEntity>>() {
            }.getType();

            mList = new Gson().fromJson(PowerPreference.getDefaultFile().getString(Constant.mList, new Gson().toJson(new ArrayList<SaveEntity>())), type);

            if (mList.get(mList.size() - 1).videoId.equalsIgnoreCase("99999"))
                mList.remove(mList.size() - 1);

        } catch (Exception e) {
            mList = new ArrayList<>();
        }

        if (!mList.isEmpty()) {
            Glide.with(this)
                    .load(mList.get(pos).imgUrl)
                    .placeholder(MyApplication.getPlaceHolder())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.frameContainer);

            binding.txtTitle.setText(Html.fromHtml(mList.get(pos).title));

            binding.includedToolbar.ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            binding.btnStart.setOnClickListener(v -> {
                uiController.gotoIntent(this, new Intent(PreviewActivity.this, PlayerActivity.class).putExtra(Constant.POSITION, pos), true, false);
            });

            binding.btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareVideo();
                }
            });

        } else {
            Constant.showToast(this, "Unknown Error Occurred");
            finish();
        }
    }


    public void shareVideo() {
        loader_dialog();
        String path = "";

        path = mList.get(pos).imgUrl;

        Glide.with(this).asBitmap().load(path)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        saveBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Toast.makeText(PreviewActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                        dismiss_loader_dialog();
                    }
                });
    }

    public void saveBitmap(Bitmap bitmap) {
        new ViewToImage(PreviewActivity.this, bitmap, new ActionListeners() {
            @Override
            public void convertedWithSuccess(Bitmap var1, String var2) {
                shareIImage(var2);
            }

            @Override
            public void convertedWithError(String var1) {
                Toast.makeText(PreviewActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                dismiss_loader_dialog();
            }
        });
    }

    public void shareIImage(String mPath) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("image/*");
            i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));

            String title = mList.get(pos).title;
            String sAux = PowerPreference.getDefaultFile().getString(Constant.vidShareMsg, "");

            String sAux2 = "https://play.google.com/store/apps/details?id=" + getPackageName();
            sAux = title + "\n\n" + sAux + "\n\n" + sAux2;

            Uri fileUri = FileProvider.getUriForFile(getApplicationContext(),
                    getPackageName() + ".fileprovider", new File(mPath));
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.putExtra(Intent.EXTRA_STREAM, fileUri);
            startActivity(Intent.createChooser(i, "Choose One"));
            dismiss_loader_dialog();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PreviewActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
            dismiss_loader_dialog();
        }

    }
}