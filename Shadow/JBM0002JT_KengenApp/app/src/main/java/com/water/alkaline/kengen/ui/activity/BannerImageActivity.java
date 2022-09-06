package com.water.alkaline.kengen.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.databinding.ActivityBannerImageBinding;
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
import com.water.alkaline.kengen.placements.MainAds;
import com.water.alkaline.kengen.ui.adapter.VpImageAdapter;
import com.water.alkaline.kengen.ui.listener.OnBannerListerner;
import com.water.alkaline.kengen.utils.Constant;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BannerImageActivity extends AppCompatActivity {

    ActivityBannerImageBinding binding;
    ArrayList<Banner> banners = new ArrayList<>();

    int POS = 0;
    String PAGE = Constant.LIVE;

    VpImageAdapter adapter;

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
        new MainAds().showBannerAds(this, null, null);
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

        dismiss_download_dialog();

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
                new LargeNativeAds().showNativeAds(BannerImageActivity.this, downloadDialog, null, null);
            }
        });
        downloadDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBannerImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        startPager();
    }

    public void startPager() {
        if (getIntent() != null && getIntent().hasExtra("PAGE")) {
            POS = getIntent().getIntExtra("POS", 0);
            PAGE = getIntent().getStringExtra("PAGE");
        }

        binding.includedToolbar.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.llmenuDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadEntity entity = null;
                if (viewModel.getDownloadbyUrl(banners.get(binding.viewpager.getCurrentItem()).getUrl()).size() > 0)
                    entity = viewModel.getDownloadbyUrl(banners.get(binding.viewpager.getCurrentItem()).getUrl()).get(0);

                if (entity != null) {
                    if (new File(entity.filePath).exists()) {
                        shareImage(entity.filePath);
                    } else {
                        downloadBanner(banners.get(binding.viewpager.getCurrentItem()), false);
                    }
                } else
                    downloadBanner(banners.get(binding.viewpager.getCurrentItem()), false);

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        }, 500);
    }

    public void refresh() {

        try {
            Type type = new TypeToken<List<Banner>>() {
            }.getType();

            banners = new Gson().fromJson(PowerPreference.getDefaultFile().getString(Constant.mList, new Gson().toJson(new ArrayList<Banner>())), type);
        } catch (Exception e) {
            banners = new ArrayList<>();
        }

        adapter = new VpImageAdapter(this, banners, new OnBannerListerner() {
            @Override
            public void onItemClick(int position, Banner item) {

            }
        });

        binding.viewpager.setAdapter(adapter);
        binding.viewpager.setCurrentItem(POS, false);
        checkDownloadIcon(binding.viewpager.getCurrentItem());
        checkArrow();
        checkData();

        binding.viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                checkDownloadIcon(position);
                checkArrow();
            }
        });

        binding.ivMenuLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.viewpager.setCurrentItem(binding.viewpager.getCurrentItem() - 1, true);
            }
        });

        binding.ivMenuRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.viewpager.setCurrentItem(binding.viewpager.getCurrentItem() + 1, true);
            }
        });
    }

    public void checkArrow() {
        if (binding.viewpager.getCurrentItem() == 0) {
            binding.ivMenuLeft.setVisibility(View.GONE);
        } else {
            binding.ivMenuLeft.setVisibility(View.VISIBLE);
        }

        if (binding.viewpager.getCurrentItem() == adapter.getItemCount() - 1) {
            binding.ivMenuRight.setVisibility(View.GONE);
        } else {
            binding.ivMenuRight.setVisibility(View.VISIBLE);
        }

    }

    public void checkData() {
        binding.includedProgress.progress.setVisibility(View.GONE);
        if (binding.viewpager.getAdapter() != null && binding.viewpager.getAdapter().getItemCount() > 0) {
            binding.cvBottom.setVisibility(View.VISIBLE);
            binding.includedProgress.llError.setVisibility(View.GONE);
        } else {
            binding.cvBottom.setVisibility(View.GONE);
        }
    }

    public void checkDownloadIcon(int pos) {
        DownloadEntity entity = null;
        if (viewModel.getDownloadbyUrl(banners.get(pos).getUrl()).size() > 0)
            entity = viewModel.getDownloadbyUrl(banners.get(pos).getUrl()).get(0);

        if (entity != null) {
            binding.ivDownload.setVisibility(View.GONE);
            binding.ivShare.setVisibility(View.VISIBLE);
        } else {
            binding.ivDownload.setVisibility(View.VISIBLE);
            binding.ivShare.setVisibility(View.GONE);
        }
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
            Constant.showToast(BannerImageActivity.this, "Something went wrong");
        }

    }

    public void downloadBanner(Banner banner, boolean isShare) {

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
                        Constant.showToast(BannerImageActivity.this, "Download Completes");
                        DownloadEntity entity = new DownloadEntity(banner.getName(), Constant.getImagedisc() + "/" + filename, Constant.getImagedisc() + "/" + filename, banner.getUrl(), Constant.TYPE_IMAGE);
                        viewModel.insertDownloads(entity);
                        checkDownloadIcon(binding.viewpager.getCurrentItem());
                        if (isShare) {
                            shareImage(Constant.getImagedisc() + "/" + filename);
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        Log.e("TAG", error.toString());
                        Constant.showToast(BannerImageActivity.this, "Something went wrong");
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
}