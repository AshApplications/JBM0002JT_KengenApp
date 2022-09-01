package com.water.alkaline.kengen.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.databinding.ActivityImageBinding;
import com.water.alkaline.kengen.databinding.DialogDownloadBinding;
import com.water.alkaline.kengen.library.downloader.Error;
import com.water.alkaline.kengen.library.downloader.OnDownloadListener;
import com.water.alkaline.kengen.library.downloader.OnProgressListener;
import com.water.alkaline.kengen.library.downloader.PRDownloader;
import com.water.alkaline.kengen.library.downloader.Progress;
import com.water.alkaline.kengen.model.DownloadEntity;
import com.water.alkaline.kengen.model.main.Banner;
import com.water.alkaline.kengen.placements.BackInterAds;
import com.water.alkaline.kengen.placements.LargeNativeAds;
import com.water.alkaline.kengen.placements.ListBannerAds;
import com.water.alkaline.kengen.utils.Constant;
import com.preference.PowerPreference;

import java.io.File;
import java.util.Objects;

public class ImageActivity extends AppCompatActivity {

    ActivityImageBinding binding;
    String mPath = "";
    Banner banner;
    DownloadEntity entity;

    public AppViewModel viewModel;
    public Dialog downloadDialog;
    public DialogDownloadBinding downloadBinding;

    public void dismiss_download_dialog() {
        if (downloadDialog != null && downloadDialog.isShowing())
            downloadDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constant.checkIcon(this);
        new ListBannerAds().showBannerAds(this, binding.includedAd.frameNativeMini, binding.includedAd.adSpaceMini);
    }

    @Override
    public void onBackPressed() {
        new BackInterAds().showInterAds(this, new BackInterAds.OnAdClosedListener() {
            @Override
            public void onAdClosed() {
                finish();
            }
        });
    }

    public void download_dialog() {
        downloadDialog = new Dialog(this, R.style.NormalDialog);
        downloadBinding = DialogDownloadBinding.inflate(getLayoutInflater());
        downloadDialog.setContentView(downloadBinding.getRoot());
        Objects.requireNonNull(downloadDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        downloadDialog.setCancelable(false);
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        downloadDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                new LargeNativeAds().showNativeAds(ImageActivity.this, downloadDialog, null, null);
            }
        });
        downloadDialog.show();

    }

    public void setBG() {
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);


        binding.includedToolbar.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setBG();

        mPath = getIntent().getExtras().getString("mpath", "");
        if (mPath.startsWith("http")) {
            banner = null;
            if (viewModel.getBannerbyUrl(mPath).size() > 0) {
                banner = viewModel.getBannerbyUrl(mPath).get(0);
                Glide.with(this).load(banner.getUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.ivImage);
            }
        } else {
            entity = null;
            if (viewModel.getDownloadbyUrl(mPath).size() > 0) {
                entity = viewModel.getDownloadbyUrl(mPath).get(0);
                Glide.with(this).load(entity.filePath).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.ivImage);
            }
        }

        checkDownload();
        listener();
    }

    public void checkDownload() {
        if (banner != null) {
            DownloadEntity entity = null;
            if (viewModel.getDownloadbyUrl(banner.getUrl()).size() > 0)
                entity = viewModel.getDownloadbyUrl(banner.getUrl()).get(0);

            if (entity != null) {
                binding.ivDownload.setVisibility(View.GONE);
                binding.ivShare.setVisibility(View.VISIBLE);
            } else {
                binding.ivDownload.setVisibility(View.VISIBLE);
                binding.ivShare.setVisibility(View.GONE);
            }
        } else {
            binding.ivDownload.setVisibility(View.GONE);
            binding.ivShare.setVisibility(View.VISIBLE);
        }
    }


    private void listener() {

        binding.llmenuDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (banner != null) {
                    DownloadEntity entity = null;
                    if (viewModel.getDownloadbyUrl(banner.getUrl()).size() > 0)
                        entity = viewModel.getDownloadbyUrl(banner.getUrl()).get(0);

                    if (entity != null) {
                        if (new File(entity.filePath).exists()) {
                            shareImage(entity.filePath);
                        } else {
                            downloadBanner(true);
                        }
                    } else {
                        downloadBanner(true);
                    }
                } else {
                    if (entity != null) {
                        if (new File(entity.filePath).exists()) {
                            shareImage(entity.filePath);
                        } else {
                            downloadBanner(true);
                        }
                    } else {
                        downloadBanner(true);
                    }
                }
            }
        });
        binding.llmenuDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (banner != null) {
                    DownloadEntity entity = null;
                    if (viewModel.getDownloadbyUrl(banner.getUrl()).size() > 0)
                        entity = viewModel.getDownloadbyUrl(banner.getUrl()).get(0);

                    if (entity != null) {
                        if (new File(entity.filePath).exists()) {
                            shareImage(entity.filePath);
                        } else {
                            downloadBanner(false);
                        }
                    } else
                        downloadBanner(false);
                } else {
                    Constant.showToast(ImageActivity.this, "File Already downloaded at " + entity.filePath);
                }
            }
        });
    }

    public void downloadBanner(boolean isShare) {

        if (!Constant.checkPermissions()) {
            Constant.getPermissions(this);
            return;
        }

        download_dialog();
        String filename = "file" + System.currentTimeMillis() + ".jpg";
        PRDownloader.download(banner.getUrl(), Constant.getImagedisc(), filename)
                .setTag(Integer.parseInt(banner.getId()))
                .build()
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        downloadBinding.txtvlu.setText("Downloading " + (int) (((double) progress.currentBytes / progress.totalBytes) * 100.0) + " %");
                    }
                }).start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        dismiss_download_dialog();
                        Constant.showToast(ImageActivity.this, "Download Completes");
                        DownloadEntity entity = new DownloadEntity(banner.getName(), Constant.getImagedisc() + "/" + filename, Constant.getImagedisc() + "/" + filename, banner.getUrl(), Constant.TYPE_IMAGE);
                        viewModel.insertDownloads(entity);
                        checkDownload();
                        if (isShare) {
                            shareImage(Constant.getImagedisc() + "/" + filename);
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        Log.e("TAG", error.toString());
                        Constant.showToast(ImageActivity.this, "Something went wrong");
                        dismiss_download_dialog();
                    }
                });

        downloadBinding.txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PRDownloader.cancel(Integer.parseInt(banner.getId()));
                dismiss_download_dialog();
            }
        });
    }

    public void shareImage(String mPath) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("image/*");
            i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));

            String sAux = PowerPreference.getDefaultFile().getString(Constant.vidShareMsg, "");
            String sAux2 = "https://play.google.com/store/apps/details?id=" + getPackageName();

            sAux = sAux + "\n\n" + sAux2;
            Uri fileUri = FileProvider.getUriForFile(getApplicationContext(),
                    getPackageName() + ".fileprovider", new File(mPath));
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.putExtra(Intent.EXTRA_STREAM, fileUri);

            startActivity(Intent.createChooser(i, "Choose One"));
        } catch (Exception e) {
            e.printStackTrace();
            Constant.showToast(ImageActivity.this, "Something went wrong");
        }

    }
}