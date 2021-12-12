package com.water.alkaline.kengen.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.data.network.RetroClient;
import com.water.alkaline.kengen.databinding.ActivityChannelBinding;
import com.water.alkaline.kengen.library.ItemOffsetDecoration;
import com.water.alkaline.kengen.model.ErrorReponse;
import com.water.alkaline.kengen.model.SaveEntity;
import com.water.alkaline.kengen.model.channel.ChannelResponse;
import com.water.alkaline.kengen.model.channel.PlaylistResponse;
import com.water.alkaline.kengen.model.main.Channel;
import com.water.alkaline.kengen.model.update.AppInfo;
import com.water.alkaline.kengen.model.update.UpdateResponse;
import com.water.alkaline.kengen.placements.BannerAds;
import com.water.alkaline.kengen.placements.InterAds;
import com.water.alkaline.kengen.ui.adapter.ChannelAdapter;
import com.water.alkaline.kengen.ui.adapter.VideosAdapter;
import com.water.alkaline.kengen.ui.listener.OnChannelListener;
import com.water.alkaline.kengen.ui.listener.OnLoadMoreListener;
import com.water.alkaline.kengen.ui.listener.OnVideoListener;
import com.water.alkaline.kengen.utils.Constant;
import com.preference.PowerPreference;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChannelActivity extends AppCompatActivity {

    ActivityChannelBinding binding;


    List<Channel> chanList = new ArrayList<>();
    ChannelAdapter channelAdapter;

    List<SaveEntity> videoList = new ArrayList<>();
    VideosAdapter videosAdapter;

    String catId;
    public AppViewModel viewModel;

    public void setBG() {
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        Glide.with(this).load(R.drawable.bg_splash).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivBG);

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
        binding = ActivityChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setBG();

        getStart();
    }

    public void getStart() {
        catId = getIntent().getExtras().getString("catId", "894398");
        chanList = viewModel.getAllChannelByCategory(catId);
        if (chanList.size() > 1) {
            Channels();
        } else {
            PowerPreference.getDefaultFile().putString(Constant.mChannelID, chanList.get(0).getYouid());
            PowerPreference.getDefaultFile().putBoolean(Constant.mIsChannel, chanList.get(0).getType().equalsIgnoreCase("0"));
            Videos();
        }
    }


    public void Channels() {
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                switch (channelAdapter.getItemViewType(i)) {
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
        binding.rvChannels.setLayoutManager(manager);
        channelAdapter = new ChannelAdapter(this, chanList, new OnChannelListener() {
            @Override
            public void onItemClick(int position, Channel item) {

                new InterAds().showInter(ChannelActivity.this, new InterAds.OnAdClosedListener() {
                    @Override
                    public void onAdClosed() {
                        PowerPreference.getDefaultFile().putString(Constant.mChannelID, item.getYouid());
                        PowerPreference.getDefaultFile().putBoolean(Constant.mIsChannel, item.getType().equalsIgnoreCase("0"));
                        startActivity(new Intent(ChannelActivity.this, VideoListActivity.class));
                    }
                });


            }
        });

        binding.rvChannels.addItemDecoration(new ItemOffsetDecoration(ChannelActivity.this, R.dimen.item_off_ten));
        binding.rvChannels.setAdapter(channelAdapter);
        binding.rvChannels.getRecycledViewPool().setMaxRecycledViews(Constant.AD_TYPE, 50);
        checkData();
    }

/*
    public void refreshActivity() {
        if (binding.rvChannels.getAdapter().getItemCount() <= 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.refreshAdapter(viewModel.getAllChannelByCategory(catId));
                    checkData();
                }
            }, 500);
        }
    }
*/

    @Override
    protected void onResume() {
        super.onResume();
        new BannerAds().showBanner(this);
    }

    public void checkData() {
        binding.includedProgress.progress.setVisibility(View.GONE);
        if (binding.rvChannels.getAdapter() != null && binding.rvChannels.getAdapter().getItemCount() > 0) {
            binding.includedProgress.llError.setVisibility(View.GONE);
        } else {
            binding.includedProgress.llError.setVisibility(View.VISIBLE);
        }
    }

    public void Videos() {
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

        binding.rvChannels.setLayoutManager(manager);
        binding.rvChannels.addItemDecoration(new ItemOffsetDecoration(ChannelActivity.this, R.dimen.item_off_ten));

        videosAdapter = new VideosAdapter(ChannelActivity.this, videoList, binding.rvChannels, new OnVideoListener() {
            @Override
            public void onItemClick(int position, SaveEntity item) {
                new InterAds().showInter(ChannelActivity.this, new InterAds.OnAdClosedListener() {
                    @Override
                    public void onAdClosed() {
                        int pos = position;
                        for (int i = 0; i < videoList.size(); i++) {
                            if (videoList.get(i).videoId.equalsIgnoreCase(item.videoId)) {
                                pos = i;
                                break;
                            }
                        }
                        PowerPreference.getDefaultFile().putString(Constant.mList, new Gson().toJson(videoList));
                        startActivity(new Intent(ChannelActivity.this, PreviewActivity.class).putExtra(Constant.POSITION, pos));
                    }
                });

            }
        });

        binding.rvChannels.setAdapter(videosAdapter);
        binding.rvChannels.getRecycledViewPool().setMaxRecycledViews(Constant.AD_TYPE, 50);
        videosAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                try {
                    videosAdapter.arrayList.add(new SaveEntity("99999", null, null, null));
                    videosAdapter.notifyItemInserted(videosAdapter.arrayList.size() - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!PowerPreference.getDefaultFile().getString(PowerPreference.getDefaultFile().getString(Constant.mChannelID), "").equalsIgnoreCase("")) {
                            if (PowerPreference.getDefaultFile().getBoolean(Constant.mIsChannel)) {
                                channelAPI();
                            } else {
                                playlistAPI();
                            }
                        } else {
                            videosAdapter.arrayList.remove(videosAdapter.arrayList.size() - 1);
                            videosAdapter.notifyItemRemoved(videosAdapter.arrayList.size());
                        }
                    }
                }, 2000);
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PowerPreference.getDefaultFile().putString(PowerPreference.getDefaultFile().getString(Constant.mChannelID), "");
                if (PowerPreference.getDefaultFile().getBoolean(Constant.mIsChannel)) {
                    channelAPI();
                } else {
                    playlistAPI();
                }
            }
        }, 2000);

    }

    public void refreshActivity() {
        videosAdapter.refreshAdapter(videoList);
        checkData();
    }

    public boolean checkInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) ChannelActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    public void channelError(String error) {
        binding.includedProgress.cvProError.setVisibility(View.INVISIBLE);
        binding.cvIerror.setVisibility(View.VISIBLE);
        binding.txtError.setText(error);
        binding.txtRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.includedProgress.cvProError.setVisibility(View.VISIBLE);
                binding.cvIerror.setVisibility(View.GONE);
                channelAPI();
            }
        });
    }

    public void channelAPI() {
        if (checkInternet()) {
            RetroClient.getInstance().getYouApi().channelApi(PowerPreference.getDefaultFile().getString(Constant.mKeyId), PowerPreference.getDefaultFile().getString(Constant.mChannelID), PowerPreference.getDefaultFile().getString(PowerPreference.getDefaultFile().getString(Constant.mChannelID), "")).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        if (response.body() != null) {
                            if (response.body().get("error") == null) {
                                final ChannelResponse channelResponse = new Gson().fromJson(response.body(), ChannelResponse.class);

                                if (videosAdapter != null && videosAdapter.arrayList.size() != 0 && videosAdapter.arrayList.get(videosAdapter.arrayList.size() - 1).videoId.equalsIgnoreCase("99999")) {
                                    videosAdapter.arrayList.remove(videosAdapter.arrayList.size() - 1);
                                    videosAdapter.notifyItemRemoved(videosAdapter.arrayList.size());
                                }

                                if (channelResponse.nextPageToken != null && !channelResponse.nextPageToken.equalsIgnoreCase("")) {
                                    PowerPreference.getDefaultFile().putString(PowerPreference.getDefaultFile().getString(Constant.mChannelID), channelResponse.nextPageToken);
                                } else {
                                    PowerPreference.getDefaultFile().putString(PowerPreference.getDefaultFile().getString(Constant.mChannelID), "");
                                }

                                for (int i = 0; i < channelResponse.items.size(); i++) {
                                    ChannelResponse.Item item = channelResponse.items.get(i);
                                    if (!item.snippet.title.equalsIgnoreCase("Private video") && !item.snippet.title.equalsIgnoreCase("Deleted video")) {
                                        String url = "http://i.ytimg.com/vi/" + item.id.videoId + "/hqdefault.jpg";
                                        SaveEntity entity = new SaveEntity(item.id.videoId, item.snippet.title, item.snippet.title, url);
                                        videoList.add(entity);
                                    }
                                }

                                refreshActivity();

                            } else {

                                ErrorReponse errorReponse = new Gson().fromJson(response.body(), ErrorReponse.class);
                                if (errorReponse.error.errors.get(0).reason.equalsIgnoreCase("quotaExceeded") ||
                                        errorReponse.error.errors.get(0).reason.equalsIgnoreCase("forbidden")) {
                                    updateAPI();
                                } else {
                                    Constant.showToast(ChannelActivity.this, "Something went Wrong");
                                    channelError("Something went Wrong");
                                }

                            }
                        } else {
                            if (response.code() == 403) {
                                updateAPI();
                            } else {
                                Constant.showToast(ChannelActivity.this, "Something went Wrong");
                                channelError("Something went Wrong");
                            }
                        }

                    } catch (Exception e) {
                        Constant.showLog(e.toString());
                        e.printStackTrace();
                        Constant.showToast(ChannelActivity.this, "Something went Wrong");
                        channelError("Something went Wrong");
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    channelError("Something went Wrong");
                }
            });
        } else {
            channelError(ChannelActivity.this.getResources().getString(R.string.error_internet));
        }
    }


    public void playlistError(String error) {
        binding.includedProgress.cvProError.setVisibility(View.INVISIBLE);
        binding.cvIerror.setVisibility(View.VISIBLE);
        binding.txtError.setText(error);
        binding.txtRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.includedProgress.cvProError.setVisibility(View.VISIBLE);
                binding.cvIerror.setVisibility(View.GONE);
                playlistAPI();
            }
        });
    }

    public void playlistAPI() {
        if (checkInternet()) {
            RetroClient.getInstance().getYouApi().playlistApi(PowerPreference.getDefaultFile().getString(Constant.mKeyId), PowerPreference.getDefaultFile().getString(Constant.mChannelID), PowerPreference.getDefaultFile().getString(PowerPreference.getDefaultFile().getString(Constant.mChannelID), "")).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        if (response.body() != null) {
                            if (response.body().get("error") == null) {
                                final PlaylistResponse playlistResponse = new Gson().fromJson(response.body(), PlaylistResponse.class);

                                if (videosAdapter != null && videosAdapter.arrayList.size() != 0 && videosAdapter.arrayList.get(videosAdapter.arrayList.size() - 1).videoId.equalsIgnoreCase("99999")) {
                                    videosAdapter.arrayList.remove(videosAdapter.arrayList.size() - 1);
                                    videosAdapter.notifyItemRemoved(videosAdapter.arrayList.size());
                                }
                                if (playlistResponse.nextPageToken != null && !playlistResponse.nextPageToken.equalsIgnoreCase("")) {
                                    PowerPreference.getDefaultFile().putString(PowerPreference.getDefaultFile().getString(Constant.mChannelID), playlistResponse.nextPageToken);
                                } else {
                                    PowerPreference.getDefaultFile().putString(PowerPreference.getDefaultFile().getString(Constant.mChannelID), "");
                                }
                                for (int i = 0; i < playlistResponse.items.size(); i++) {
                                    PlaylistResponse.Item item = playlistResponse.items.get(i);
                                    if (!item.snippet.title.equalsIgnoreCase("Private video") && !item.snippet.title.equalsIgnoreCase("Deleted video")) {
                                        String url = "http://i.ytimg.com/vi/" + item.snippet.resourceId.videoId + "/hqdefault.jpg";
                                        SaveEntity entity = new SaveEntity(item.snippet.resourceId.videoId, item.snippet.title, item.snippet.title, url);
                                        videoList.add(entity);
                                    }
                                }
                                refreshActivity();
                            } else {

                                ErrorReponse errorReponse = new Gson().fromJson(response.body(), ErrorReponse.class);
                                if (errorReponse.error.errors.get(0).reason.equalsIgnoreCase("quotaExceeded") ||
                                        errorReponse.error.errors.get(0).reason.equalsIgnoreCase("forbidden")) {
                                    updateAPI();
                                } else {
                                    Constant.showToast(ChannelActivity.this, "Something went Wrong");
                                    playlistError("Something went Wrong");
                                }
                            }
                        } else {

                            if (response.code() == 403) {
                                updateAPI();
                            } else {
                                Constant.showToast(ChannelActivity.this, "Something went Wrong");
                                playlistError("Something went Wrong");
                            }

                        }
                    } catch (Exception e) {
                        Constant.showLog(e.toString());
                        e.printStackTrace();
                        Constant.showToast(ChannelActivity.this, "Something went Wrong");
                        playlistError("Something went Wrong");
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    playlistError("Something went Wrong");
                }
            });
        } else {
            playlistError(ChannelActivity.this.getResources().getString(R.string.error_internet));
        }
    }

    public void updateError(String error) {
        binding.includedProgress.cvProError.setVisibility(View.INVISIBLE);
        binding.cvIerror.setVisibility(View.VISIBLE);
        binding.txtError.setText(error);
        binding.txtRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.includedProgress.cvProError.setVisibility(View.VISIBLE);
                binding.cvIerror.setVisibility(View.GONE);
                updateAPI();
            }
        });
    }

    public void update2Error(String error) {
        binding.includedProgress.cvProError.setVisibility(View.INVISIBLE);
        binding.cvIerror.setVisibility(View.VISIBLE);
        binding.txtError.setText(error);
        binding.txtRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.includedProgress.cvProError.setVisibility(View.VISIBLE);
                binding.cvIerror.setVisibility(View.GONE);
                if (PowerPreference.getDefaultFile().getBoolean(Constant.mIsChannel)) {
                    channelAPI();
                } else {
                    playlistAPI();
                }
            }
        });
    }


    public void updateAPI() {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.mIsApi, false)) {
            update2Error("Please wait Sometimes");
            return;
        }

        if (checkInternet()) {
            PowerPreference.getDefaultFile().putBoolean(Constant.mIsApi, true);
            RetroClient.getInstance().getApi().updateApi("refresh")
                    .enqueue(new Callback<UpdateResponse>() {
                        @Override
                        public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                            try {
                                PowerPreference.getDefaultFile().putBoolean(Constant.mIsApi, false);
                                final UpdateResponse updateResponse = response.body();

                                AppInfo appInfo = updateResponse.getData().getAppInfo().get(0);
                                PowerPreference.getDefaultFile().putString(Constant.mKeyId, appInfo.getApiKey());

                                if (PowerPreference.getDefaultFile().getBoolean(Constant.mIsChannel)) {
                                    channelAPI();
                                } else {
                                    playlistAPI();
                                }

                            } catch (Exception e) {
                                Constant.showLog(e.toString());
                                e.printStackTrace();
                                Constant.showToast(ChannelActivity.this, "Something went Wrong");
                                updateError("Something went Wrong");
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateResponse> call, Throwable t) {
                            updateError("Something went Wrong");
                        }
                    });
        } else {
            updateError(ChannelActivity.this.getResources().getString(R.string.error_internet));
        }
    }


}