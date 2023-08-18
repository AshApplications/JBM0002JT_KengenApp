package com.water.alkaline.kengen.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiContext;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gms.ads.MainAds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.DefaultPlayerUiController;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.databinding.ActivityPlayerBinding;
import com.water.alkaline.kengen.databinding.DialogLoadingBinding;
import com.water.alkaline.kengen.library.ActionListeners;
import com.water.alkaline.kengen.library.ViewToImage;
import com.water.alkaline.kengen.model.SaveEntity;
import com.google.gms.ads.BackInterAds;
import com.water.alkaline.kengen.ui.adapter.VideosAdapter;
import com.water.alkaline.kengen.ui.listener.OnVideoListener;
import com.water.alkaline.kengen.utils.Constant;
import com.preference.PowerPreference;


import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class PlayerActivity extends AppCompatActivity {

    ActivityPlayerBinding binding;

    List<SaveEntity> mList = new ArrayList<>();
    boolean isFullScreen = false;

    VideosAdapter videosAdapter;

    YouTubePlayer vPlayer;
    boolean isPause = false;

    AppViewModel viewModel;
    int position = 0;

    Dialog loaderDialog;

    public void dismiss_loader_dialog() {
        if (loaderDialog != null && loaderDialog.isShowing()) {
            loaderDialog.dismiss();
        }
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = MyApplication.getInstance().getViewModel();
        if (getIntent() != null && getIntent().hasExtra(Constant.POSITION)) {
            position = getIntent().getIntExtra(Constant.POSITION, 0);
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

        checkLike();
        setSize();
        setAdapter();
        setPlayer();
    }

    public void setSize() {

        binding.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareVideo();
            }
        });

        binding.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    SaveEntity entity = mList.get(position);
                    SaveEntity entity2 = null;
                    if (viewModel.getSavebyVideoId(entity.videoId).size() > 0)
                        entity2 = viewModel.getSavebyVideoId(entity.videoId).get(0);

                    if (entity2 != null) {
                        viewModel.deleteSaves(entity2);
                        checkLike();
                        if (SaveActivity.saveActivity != null)
                            SaveActivity.saveActivity.refreshData();
                    } else {
                        SaveEntity entity1 = new SaveEntity(entity.videoId, entity.title, entity.des, entity.imgUrl);
                        viewModel.insertSaves(entity1);
                        binding.ivLike.setSpeed(100f);
                        binding.ivLike.playAnimation();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void checkLike() {

        try {
            SaveEntity entity = mList.get(position);
            SaveEntity entity2 = null;
            if (viewModel.getSavebyVideoId(entity.videoId).size() > 0)
                entity2 = viewModel.getSavebyVideoId(entity.videoId).get(0);

            if (entity2 != null) {
                binding.ivLike.setProgress(1);
            } else {
                binding.ivLike.setProgress(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void setAdapter() {
        videosAdapter = new VideosAdapter(this, mList, null, new OnVideoListener() {
            @Override
            public void onItemClick(int pos, SaveEntity item) {
                position = pos;
                loadVideo(item.videoId);
            }
        });
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                switch (videosAdapter.getItemViewType(i)) {
                    case Constant.STORE_TYPE:
                        return 1;
                    case Constant.AD_TYPE:
                        return 2;
                    case Constant.LOADING:
                        return 1;
                    default:
                        return 1;

                }
            }
        });
        binding.rvVideos.setLayoutManager(manager);
        binding.rvVideos.setAdapter(videosAdapter);
        binding.rvVideos.setItemViewCacheSize(100);
        binding.rvVideos.scrollToPosition(position);

        refreshActivity();
    }

    public void refreshActivity() {
        videosAdapter.refreshAdapter(mList);
        binding.includedProgress.progress.setVisibility(View.GONE);
        checkData();
    }

    public void checkData() {
        if (binding.rvVideos.getAdapter().getItemCount() > 0) {
            binding.includedProgress.llError.setVisibility(View.GONE);
        } else {
            binding.includedProgress.llError.setVisibility(View.VISIBLE);
        }
    }


    public void setPlayer() {

        getLifecycle().addObserver(binding.playerView);
        binding.playerView.setEnableAutomaticInitialization(false);
        IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(0).rel(0).build();
        binding.playerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                DefaultPlayerUiController defaultPlayerUiController = new DefaultPlayerUiController(binding.playerView, youTubePlayer);
                defaultPlayerUiController.showYouTubeButton(false);
                defaultPlayerUiController.setFullscreenButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isFullScreen) {
                            isFullScreen = false;
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            binding.cvToolbar.setVisibility(View.VISIBLE);
                            binding.playerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        } else {
                            isFullScreen = true;
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            binding.cvToolbar.setVisibility(View.GONE);
                            binding.playerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        }
                    }
                });
                binding.playerView.setCustomPlayerUi(defaultPlayerUiController.getRootView());
                vPlayer = youTubePlayer;
                vPlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError error) {
                        super.onError(youTubePlayer, error);
                        Log.e("TAG", error.toString());
                        Constant.showToast(PlayerActivity.this, "Something went wrong");
                        nextVideo();
                    }


                    @Override
                    public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                        super.onStateChange(youTubePlayer, state);
                        if (state == PlayerConstants.PlayerState.ENDED) {
                            nextVideo();
                        }
                    }
                });
                loadVideo(mList.get(position).videoId);
            }
        }, options);
    }


    public void nextVideo() {
        int mPos = position;
        if (mPos + 1 < mList.size()) {
            position = position + 1;
            loadVideo(mList.get(position).videoId);
        } else {
            Constant.showToast(PlayerActivity.this, "Completed All Videos");
            vPlayer.seekTo(0);
            vPlayer.pause();
        }
    }

    public void loadVideo(String id) {
        if (vPlayer != null) {
            checkLike();
            try {
                vPlayer.loadVideo(id, 0);
            } catch (IllegalStateException e) {
                setPlayer();
            }
        } else {
            Constant.showToast(PlayerActivity.this, "VideoPlayer not loaded");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (vPlayer != null) {
            try {
                vPlayer.pause();
                isPause = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPause) {
            isPause = false;
            if (vPlayer != null) {
                try {
                    vPlayer.play();
                } catch (Exception e) {
                    e.printStackTrace();
                    setPlayer();
                }
            } else {
                setPlayer();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isFullScreen) {
            isFullScreen = false;
            binding.cvToolbar.setVisibility(View.VISIBLE);
            binding.playerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            new MainAds().showBackInterAds(this, this::finish);
        }
    }


    public void shareVideo() {
        loader_dialog();
        String path = "";

        path = mList.get(position).imgUrl;

        Glide.with(this).asBitmap().load(path)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        saveBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Toast.makeText(PlayerActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                        dismiss_loader_dialog();
                    }
                });
    }

    public void saveBitmap(Bitmap bitmap) {
        new ViewToImage(PlayerActivity.this, bitmap, new ActionListeners() {
            @Override
            public void convertedWithSuccess(Bitmap var1, String var2) {
                shareIImage(var2);
            }

            @Override
            public void convertedWithError(String var1) {
                Toast.makeText(PlayerActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                dismiss_loader_dialog();
            }
        });
    }

    public void shareIImage(String mPath) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("image/*");
            i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));

            String title = mList.get(position).title;
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
            Toast.makeText(PlayerActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
            dismiss_loader_dialog();
        }
    }
}