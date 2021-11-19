package com.water.alkaline.kengen.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.databinding.ActivityPlayerBinding;
import com.water.alkaline.kengen.databinding.DialogLoadingBinding;
import com.water.alkaline.kengen.library.ActionListeners;
import com.water.alkaline.kengen.library.ItemOffsetDecoration;
import com.water.alkaline.kengen.library.ViewToImage;
import com.water.alkaline.kengen.model.SaveEntity;
import com.water.alkaline.kengen.model.feedback.Feedback;
import com.water.alkaline.kengen.ui.adapter.VideosAdapter;
import com.water.alkaline.kengen.ui.listener.OnVideoListener;
import com.water.alkaline.kengen.utils.Constant;
import com.preference.PowerPreference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerActivity extends YouTubeBaseActivity {

    ActivityPlayerBinding binding;

    List<SaveEntity> mList = new ArrayList<>();
    boolean isFullScreen = false;

    VideosAdapter videosAdapter;

    boolean isPause = false;
    YouTubePlayer vPlayer;

    Dialog loaderDialog;
    AppViewModel viewModel;
    YouTubePlayerFragment playerFragment;

    public void dismiss_loader_dialog() {
        if (loaderDialog != null && loaderDialog.isShowing())
            loaderDialog.dismiss();
    }

    public void loader_dialog() {
        loaderDialog = new Dialog(this, R.style.NormalDialog);
        DialogLoadingBinding loadingBinding = DialogLoadingBinding.inflate(getLayoutInflater());
        loaderDialog.setContentView(loadingBinding.getRoot());
        Objects.requireNonNull(loaderDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loaderDialog.setCancelable(false);
        loaderDialog.setCanceledOnTouchOutside(false);
        loaderDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loaderDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = MyApplication.getInstance().getViewModel();

        try {
            Type type = new TypeToken<List<SaveEntity>>() {
            }.getType();

            mList = new Gson().fromJson(PowerPreference.getDefaultFile().getString(Constant.mList, new Gson().toJson(new ArrayList<SaveEntity>())), type);
            if (mList.get(mList.size() - 1) == null)
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
        Glide.with(this).load(R.drawable.bg).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivBG);


        binding.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareVideo();
            }
        });

        binding.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveEntity entity = mList.get(PowerPreference.getDefaultFile().getInt(Constant.mPosition, 0));
                SaveEntity entity2 = null;
                if (viewModel.getSavebyVideoId(entity.videoId).size() > 0)
                    entity2 = viewModel.getSavebyVideoId(entity.videoId).get(0);

                if (entity2 != null) {
                    viewModel.deleteSaves(entity2);
                    checkLike();
                    if (SaveActivity.saveActivity != null)
                        SaveActivity.saveActivity.refreshData();
                } else {
                    SaveEntity entity1 = new SaveEntity(entity.videoId, entity.title, entity.imgUrl);
                    viewModel.insertSaves(entity1);
                    binding.ivLike.setSpeed(100f);
                    binding.ivLike.playAnimation();
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

        SaveEntity entity = mList.get(PowerPreference.getDefaultFile().getInt(Constant.mPosition, 0));
        SaveEntity entity2 = null;
        if (viewModel.getSavebyVideoId(entity.videoId).size() > 0)
            entity2 = viewModel.getSavebyVideoId(entity.videoId).get(0);

        if (entity2 != null) {
            binding.ivLike.setProgress(1);
        } else {
            binding.ivLike.setProgress(0);
        }

    }

    public void setAdapter() {


        videosAdapter = new VideosAdapter(this, mList, null, new OnVideoListener() {
            @Override
            public void onItemClick(int position, SaveEntity item) {
                PowerPreference.getDefaultFile().putInt(Constant.mPosition, position);
                loadVideo(item.videoId);
            }
        });
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.rvVideos.setLayoutManager(manager);
        binding.rvVideos.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.item_off_ten));
        binding.rvVideos.setAdapter(videosAdapter);
        binding.rvVideos.scrollToPosition(PowerPreference.getDefaultFile().getInt(Constant.mPosition, 0));

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


    public void initialize(String videoID) {
        playerFragment.initialize(PowerPreference.getDefaultFile().getString(Constant.mKeyId), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if (!b) {
                    PlayerActivity.this.vPlayer = youTubePlayer;
                    vPlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                        @Override
                        public void onLoading() {

                        }

                        @Override
                        public void onLoaded(String s) {

                        }

                        @Override
                        public void onAdStarted() {

                        }

                        @Override
                        public void onVideoStarted() {

                        }

                        @Override
                        public void onVideoEnded() {
                            nextVideo();
                        }

                        @Override
                        public void onError(YouTubePlayer.ErrorReason errorReason) {
                            Log.e("TAG", errorReason.toString());
                            Constant.showToast(PlayerActivity.this, "Something went wrong");
                            nextVideo();
                        }

                    });

                    vPlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean b) {
                            if (b) {
                                isFullScreen = true;
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            } else {
                                isFullScreen = false;
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            }
                        }
                    });
                    if (videoID == null) {
                        loadVideo(mList.get(PowerPreference.getDefaultFile().getInt(Constant.mPosition, 0)).videoId);
                    } else {
                        loadVideo(videoID);
                    }
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Constant.showToast(PlayerActivity.this, "Something went wrong");

            }
        });
    }

    public void setPlayer() {

        FragmentManager fm = getFragmentManager();
        String tag = YouTubePlayerFragment.class.getSimpleName();
        playerFragment = (YouTubePlayerFragment) fm.findFragmentByTag(tag);
        if (playerFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            playerFragment = YouTubePlayerFragment.newInstance();
            ft.replace(R.id.frameContainer, playerFragment, tag);
            ft.commit();
        }

        initialize(null);

    }


    public void nextVideo() {
        int mPos = PowerPreference.getDefaultFile().getInt(Constant.mPosition);
        if (mPos + 1 < mList.size()) {
            PowerPreference.getDefaultFile().putInt(Constant.mPosition, mPos + 1);
            loadVideo(mList.get(mPos + 1).videoId);
        } else {
            Constant.showToast(PlayerActivity.this, "Completed All Videos");
        }
    }

    public void loadVideo(String id) {
        if (vPlayer != null) {
            checkLike();
            try {
                vPlayer.loadVideo(id);
            } catch (IllegalStateException e) {
                initialize(id);
            }

        } else {
            Constant.showToast(PlayerActivity.this, "VideoPlayer not loaded");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (vPlayer != null) {
            if (vPlayer.isPlaying()) {
                vPlayer.pause();
                isPause = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPause) {
            isPause = false;
            if (vPlayer != null) {
                vPlayer.play();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isFullScreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            super.onBackPressed();
        }
    }


    public void shareVideo() {
        loader_dialog();
        String path = "";

        path = mList.get(PowerPreference.getDefaultFile().getInt(Constant.mPosition, 0)).imgUrl;

        Glide.with(this).asBitmap().load(path)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                        saveBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {
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

            String title = mList.get(PowerPreference.getDefaultFile().getInt(Constant.mPosition, 0)).title;
            String sAux = title + "\n\nI recommend Kengen app which gives you all the information like Kengen Demo, Business Plan, Results Photo & Videos, Events video, Motivational video, Forms, PPT, PDF etc.\n" +
                    "\n" +
                    "So Download & share this app into your growth line, leaders and work easily and smartly and achieve your target as soon as possible. \n\n";

            String sAux2 = "https://play.google.com/store/apps/details?id=" + getPackageName();
            sAux = sAux + sAux2;

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






/*    public void getMore()
    {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.mIsChannel)) {
            channelAPI();
        } else {
            playlistAPI();
        }

    }*/
}