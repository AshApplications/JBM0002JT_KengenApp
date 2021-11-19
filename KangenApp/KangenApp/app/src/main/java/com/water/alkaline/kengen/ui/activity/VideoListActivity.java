package com.water.alkaline.kengen.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.data.network.RetroClient;
import com.water.alkaline.kengen.databinding.ActivityVideoListBinding;
import com.water.alkaline.kengen.library.ItemOffsetDecoration;
import com.water.alkaline.kengen.model.ErrorReponse;
import com.water.alkaline.kengen.model.SaveEntity;
import com.water.alkaline.kengen.model.channel.ChannelResponse;
import com.water.alkaline.kengen.model.channel.PlaylistResponse;
import com.water.alkaline.kengen.model.update.AppInfo;
import com.water.alkaline.kengen.model.update.UpdateResponse;
import com.water.alkaline.kengen.ui.adapter.VideosAdapter;
import com.water.alkaline.kengen.ui.listener.OnLoadMoreListener;
import com.water.alkaline.kengen.ui.listener.OnVideoListener;
import com.water.alkaline.kengen.utils.Constant;
import com.preference.PowerPreference;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoListActivity extends AppCompatActivity {
    ActivityVideoListBinding binding;

    List<SaveEntity> list = new ArrayList<>();
    VideosAdapter adapter;
    public AppViewModel viewModel;

    public void setBG() {
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        Glide.with(this).load(R.drawable.bg).diskCacheStrategy(DiskCacheStrategy.ALL)
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
        binding = ActivityVideoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setBG();

        getStart();
    }

    public void getStart() {
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.rvVideos.setLayoutManager(manager);
        binding.rvVideos.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.item_off_ten));

        adapter = new VideosAdapter(this, list, binding.rvVideos, new OnVideoListener() {
            @Override
            public void onItemClick(int position, SaveEntity item) {
                PowerPreference.getDefaultFile().putString(Constant.mList, new Gson().toJson(list));
                PowerPreference.getDefaultFile().putInt(Constant.mPosition, position);
                startActivity(new Intent(VideoListActivity.this, PlayerActivity.class));
            }
        });

        binding.rvVideos.setAdapter(adapter);
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                adapter.arrayList.add(null);
                adapter.notifyItemInserted(adapter.arrayList.size() - 1);

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
                            adapter.arrayList.remove(adapter.arrayList.size() - 1);
                            adapter.notifyItemRemoved(adapter.arrayList.size());
                        }
                    }
                }, 2000);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PowerPreference.getDefaultFile().getBoolean(Constant.mIsChannel)) {
                    channelAPI();
                } else {
                    playlistAPI();
                }
            }
        },2000);

    }

    public void refreshActivity() {
        adapter.refreshAdapter(list);
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
        if (Constant.checkInternet(VideoListActivity.this)) {
            RetroClient.getInstance().getYouApi().channelApi(PowerPreference.getDefaultFile().getString(Constant.mKeyId), PowerPreference.getDefaultFile().getString(Constant.mChannelID), PowerPreference.getDefaultFile().getString(PowerPreference.getDefaultFile().getString(Constant.mChannelID), "")).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        if (response.body() != null) {
                            if (response.body().get("error") == null) {
                                final ChannelResponse channelResponse = new Gson().fromJson(response.body(), ChannelResponse.class);

                                if (adapter.arrayList.size() != 0) {
                                    adapter.arrayList.remove(adapter.arrayList.size() - 1);
                                    adapter.notifyItemRemoved(adapter.arrayList.size());
                                }

                                if (channelResponse.nextPageToken != null && !channelResponse.nextPageToken.equalsIgnoreCase("")) {
                                    PowerPreference.getDefaultFile().putString(PowerPreference.getDefaultFile().getString(Constant.mChannelID), channelResponse.nextPageToken);
                                } else {
                                    PowerPreference.getDefaultFile().putString(PowerPreference.getDefaultFile().getString(Constant.mChannelID), "");
                                }
                                for (int i = 0; i < channelResponse.items.size(); i++) {
                                    ChannelResponse.Item item = channelResponse.items.get(i);
                                    if (!item.snippet.title.equalsIgnoreCase("Private video")) {
                                        SaveEntity entity = new SaveEntity(item.id.videoId, item.snippet.title, item.snippet.thumbnails._default.url);
                                        list.add(entity);
                                    }
                                }
                                refreshActivity();
                            } else {

                                ErrorReponse errorReponse = new Gson().fromJson(response.body(), ErrorReponse.class);
                                if (errorReponse.error.errors.get(0).reason.equalsIgnoreCase("quotaExceeded") ||
                                        errorReponse.error.errors.get(0).reason.equalsIgnoreCase("forbidden")) {
                                    updateAPI();
                                } else {
                                    Constant.showToast(VideoListActivity.this, "Something went Wrong");
                                    channelError("Something went Wrong");
                                }
                            }
                        } else {
                            if (response.code() == 403) {
                                updateAPI();
                            } else {
                                Constant.showToast(VideoListActivity.this, "Something went Wrong");
                                channelError("Something went Wrong");
                            }

                        }

                    } catch (Exception e) {
                        Constant.showLog(e.toString());
                        e.printStackTrace();
                        Constant.showToast(VideoListActivity.this, "Something went Wrong");
                        channelError("Something went Wrong");
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    channelError("Something went Wrong");
                }
            });
        } else {
            channelError(getResources().getString(R.string.error_internet));
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
        if (Constant.checkInternet(VideoListActivity.this)) {
            RetroClient.getInstance().getYouApi().playlistApi(PowerPreference.getDefaultFile().getString(Constant.mKeyId), PowerPreference.getDefaultFile().getString(Constant.mChannelID), PowerPreference.getDefaultFile().getString(PowerPreference.getDefaultFile().getString(Constant.mChannelID), "")).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        if (response.body() != null) {
                            if (response.body().get("error") == null) {
                                final PlaylistResponse playlistResponse = new Gson().fromJson(response.body(), PlaylistResponse.class);

                                if (adapter.arrayList.size() != 0) {
                                    adapter.arrayList.remove(adapter.arrayList.size() - 1);
                                    adapter.notifyItemRemoved(adapter.arrayList.size());
                                }

                                if (playlistResponse.nextPageToken != null && !playlistResponse.nextPageToken.equalsIgnoreCase("")) {
                                    PowerPreference.getDefaultFile().putString(PowerPreference.getDefaultFile().getString(Constant.mChannelID), playlistResponse.nextPageToken);
                                } else {
                                    PowerPreference.getDefaultFile().putString(PowerPreference.getDefaultFile().getString(Constant.mChannelID), "");
                                }
                                for (int i = 0; i < playlistResponse.items.size(); i++) {
                                    PlaylistResponse.Item item = playlistResponse.items.get(i);
                                    if (!item.snippet.title.equalsIgnoreCase("Private video")) {
                                        SaveEntity entity = new SaveEntity(item.snippet.resourceId.videoId, item.snippet.title, item.snippet.thumbnails._default.url);
                                        list.add(entity);
                                    }
                                }
                                refreshActivity();
                            } else {

                                ErrorReponse errorReponse = new Gson().fromJson(response.body(), ErrorReponse.class);
                                if (errorReponse.error.errors.get(0).reason.equalsIgnoreCase("quotaExceeded") ||
                                        errorReponse.error.errors.get(0).reason.equalsIgnoreCase("forbidden")) {
                                    updateAPI();
                                } else {
                                    Constant.showToast(VideoListActivity.this, "Something went Wrong");
                                    playlistError("Something went Wrong");
                                }

                            }
                        } else {
                            if (response.code() == 403) {
                                updateAPI();
                            } else {
                                Constant.showToast(VideoListActivity.this, "Something went Wrong");
                                playlistError("Something went Wrong");
                            }

                        }
                    } catch (Exception e) {
                        Constant.showLog(e.toString());
                        e.printStackTrace();
                        Constant.showToast(VideoListActivity.this, "Something went Wrong");
                        playlistError("Something went Wrong");
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    playlistError("Something went Wrong");
                }
            });
        } else {
            playlistError(getResources().getString(R.string.error_internet));
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

        if (Constant.checkInternet(VideoListActivity.this)) {
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
                                Constant.showToast(VideoListActivity.this, "Something went Wrong");
                                updateError("Something went Wrong");
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateResponse> call, Throwable t) {
                            updateError("Something went Wrong");
                        }
                    });
        } else {
            updateError(getResources().getString(R.string.error_internet));
        }
    }


}