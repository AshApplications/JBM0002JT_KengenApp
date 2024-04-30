package com.water.alkaline.kengen.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;

import com.google.gms.ads.AdLoader;
import com.google.gms.ads.MyApp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.water.alkaline.kengen.BuildConfig;
import com.water.alkaline.kengen.Encrypt.DecryptEncrypt;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.data.network.RetroClient;
import com.water.alkaline.kengen.databinding.ActivityVideoListBinding;
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
import com.water.alkaline.kengen.utils.uiController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoListActivity extends AppCompatActivity {

    ActivityVideoListBinding binding;

    List<SaveEntity> list = new ArrayList<>();
    VideosAdapter adapter;
    public AppViewModel viewModel;

    @Override
    public void onBackPressed() {
        uiController.onBackPressed(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApp.getAdModel().getAdsOnOff().equalsIgnoreCase("Yes")) {
            if (binding.includedAd.flAd.getChildCount() <= 0) {
                AdLoader.getInstance().showUniversalAd(this, binding.includedAd, false);
            }
        } else {
            binding.includedAd.cvAdMain.setVisibility(View.GONE);
            binding.includedAd.flAd.setVisibility(View.GONE);
            refreshFragment();
        }
    }


    public void setBG() {
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        binding.includedToolbar.ivBack.setOnClickListener(v -> onBackPressed());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setBG();
        getStart();
    }

    public void refreshFragment() {
        if (!list.isEmpty() && adapter != null) {
            adapter.refreshAdapter(list);
        }
    }
    public void getStart() {
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                switch (adapter.getItemViewType(i)) {
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
        adapter = new VideosAdapter(this, list, binding.rvVideos, new OnVideoListener() {
            @Override
            public void onItemClick(int position, SaveEntity item) {
                list.removeAll(Collections.singleton(null));
                int pos = position;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).videoId.equalsIgnoreCase(item.videoId)) {
                        pos = i;
                        break;
                    }
                }
                PowerPreference.getDefaultFile().putString(Constant.mList, new Gson().toJson(list));
                uiController.sendIntent(VideoListActivity.this,new Intent(VideoListActivity.this, PreviewActivity.class).putExtra(Constant.POSITION, pos),true,false);
            }
        });

        binding.rvVideos.setAdapter(adapter);
        binding.rvVideos.setItemViewCacheSize(100);
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                try {
                    adapter.arrayList.add(new SaveEntity("99999", null, null, null));
                    adapter.notifyItemInserted(adapter.arrayList.size() - 1);
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
        adapter.refreshAdapter(list);
        checkData();
    }

    public void checkData() {
        binding.includedProgress.progress.setVisibility(View.GONE);
        if (binding.rvVideos.getAdapter() != null && binding.rvVideos.getAdapter().getItemCount() > 0) {
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
            RetroClient.getInstance(this).getYouApi().channelApi(PowerPreference.getDefaultFile().getString(Constant.mKeyId), PowerPreference.getDefaultFile().getString(Constant.mChannelID), PowerPreference.getDefaultFile().getString(PowerPreference.getDefaultFile().getString(Constant.mChannelID), "")).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        if (response.body() != null) {
                            if (response.body().get("error") == null) {
                                final ChannelResponse channelResponse = new Gson().fromJson(response.body(), ChannelResponse.class);

                                if (adapter != null && adapter.arrayList.size() != 0 && adapter.arrayList.get(adapter.arrayList.size() - 1).videoId.equalsIgnoreCase("99999")) {
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
                                    if (!item.snippet.title.equalsIgnoreCase("Private video") && !item.snippet.title.equalsIgnoreCase("Deleted video")) {
                                        String url = "http://i.ytimg.com/vi/" + item.id.videoId + "/hqdefault.jpg";
                                        SaveEntity entity = new SaveEntity(item.id.videoId, item.snippet.title, item.snippet.title, url);
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
            RetroClient.getInstance(this).getYouApi().playlistApi(PowerPreference.getDefaultFile().getString(Constant.mKeyId), PowerPreference.getDefaultFile().getString(Constant.mChannelID), PowerPreference.getDefaultFile().getString(PowerPreference.getDefaultFile().getString(Constant.mChannelID), "")).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        if (response.body() != null) {
                            if (response.body().get("error") == null) {
                                final PlaylistResponse playlistResponse = new Gson().fromJson(response.body(), PlaylistResponse.class);

                                if (adapter != null && adapter.arrayList.size() != 0 && adapter.arrayList.get(adapter.arrayList.size() - 1).videoId.equalsIgnoreCase("99999")) {
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
                                    if (!item.snippet.title.equalsIgnoreCase("Private video") && !item.snippet.title.equalsIgnoreCase("Deleted video")) {
                                        String url = "http://i.ytimg.com/vi/" + item.snippet.resourceId.videoId + "/hqdefault.jpg";
                                        SaveEntity entity = new SaveEntity(item.snippet.resourceId.videoId, item.snippet.title, item.snippet.title, url);
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
            int VERSION = 0;
            @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String token = PowerPreference.getDefaultFile().getString(Constant.Token, "123abc");

            PackageManager manager = getPackageManager();
            PackageInfo info = null;

            try {
                info = manager.getPackageInfo(getPackageName(), 0);
                VERSION = info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Constant.showLog(e.toString());
                VERSION = BuildConfig.VERSION_CODE;
            }

            PowerPreference.getDefaultFile().putBoolean(Constant.mIsApi, true);
            RetroClient.getInstance(this).getApi().updateApi(DecryptEncrypt.EncryptStr(this, MyApplication.updateApi(this, deviceId, token, getPackageName(), String.valueOf(VERSION), "refresh")))
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                PowerPreference.getDefaultFile().putBoolean(Constant.mIsApi, false);
                                final UpdateResponse updateResponse = new GsonBuilder().create().fromJson((DecryptEncrypt.DecryptStr(VideoListActivity.this,response.body().string())), UpdateResponse.class);
                                if (updateResponse.getFlag()) {
                                    AppInfo appInfo = updateResponse.getData().getAppInfo().get(0);
                                    PowerPreference.getDefaultFile().putString(Constant.mKeyId, appInfo.getApiKey());

                                    if (PowerPreference.getDefaultFile().getBoolean(Constant.mIsChannel)) {
                                        channelAPI();
                                    } else {
                                        playlistAPI();
                                    }
                                } else {
                                    Constant.showToast(VideoListActivity.this, "Something went Wrong");
                                }
                            } catch (Exception e) {
                                Constant.showLog(e.toString());
                                e.printStackTrace();
                                Constant.showToast(VideoListActivity.this, "Something went Wrong");
                                updateError("Something went Wrong");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            updateError("Something went Wrong");
                        }
                    });
        } else {
            updateError(getResources().getString(R.string.error_internet));
        }
    }


}