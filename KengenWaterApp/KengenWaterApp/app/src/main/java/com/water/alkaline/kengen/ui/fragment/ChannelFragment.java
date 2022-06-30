package com.water.alkaline.kengen.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.water.alkaline.kengen.BuildConfig;
import com.water.alkaline.kengen.Encrypt.DecryptEncrypt;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.data.network.RetroClient;
import com.water.alkaline.kengen.databinding.FragmentChannelBinding;
import com.water.alkaline.kengen.library.ItemOffsetDecoration;
import com.water.alkaline.kengen.model.ErrorReponse;
import com.water.alkaline.kengen.model.SaveEntity;
import com.water.alkaline.kengen.model.channel.ChannelResponse;
import com.water.alkaline.kengen.model.channel.PlaylistResponse;
import com.water.alkaline.kengen.model.main.Channel;
import com.water.alkaline.kengen.model.main.Subcategory;
import com.water.alkaline.kengen.model.update.AppInfo;
import com.water.alkaline.kengen.model.update.UpdateResponse;
import com.water.alkaline.kengen.placements.InterAds;
import com.water.alkaline.kengen.ui.activity.ChannelActivity;
import com.water.alkaline.kengen.ui.activity.HomeActivity;
import com.water.alkaline.kengen.ui.activity.PreviewActivity;
import com.water.alkaline.kengen.ui.activity.VideoListActivity;
import com.water.alkaline.kengen.ui.adapter.ChannelAdapter;
import com.water.alkaline.kengen.ui.adapter.SubcatAdapter;
import com.water.alkaline.kengen.ui.adapter.VideosAdapter;
import com.water.alkaline.kengen.ui.listener.OnChannelListener;
import com.water.alkaline.kengen.ui.listener.OnLoadMoreListener;
import com.water.alkaline.kengen.ui.listener.OnSubcatListener;
import com.water.alkaline.kengen.ui.listener.OnVideoListener;
import com.water.alkaline.kengen.utils.Constant;
import com.preference.PowerPreference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChannelFragment extends Fragment {


    public Dialog dialog;

    String channelId;
    boolean isChannel = true;
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    FragmentChannelBinding binding;
    Activity activity;

    List<Subcategory> subList = new ArrayList<>();
    List<Channel> chanList = new ArrayList<>();
    List<SaveEntity> videoList = new ArrayList<>();

    SubcatAdapter subcatAdapter;
    ChannelAdapter channelAdapter;
    VideosAdapter videosAdapter;

    public AppViewModel viewModel;

    public ChannelFragment() {
    }

    public ChannelFragment(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        if (activity == null) {
            activity = (HomeActivity) context;
        }
    }


    public static ChannelFragment newInstance(Activity activity, String param1) {
        ChannelFragment fragment = new ChannelFragment(activity);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanseState) {
        binding = FragmentChannelBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);


        if (activity != null) {
            subList = viewModel.getAllSubByCategory(mParam1);
            if (subList.size() > 1) {
                SubCategory();
            } else if (subList.size() == 1) {
                chanList = viewModel.getAllChannelByCategory(subList.get(0).getId());
                if (chanList.size() > 1) {
                    Channels();
                } else {
                    channelId = chanList.get(0).getYouid();
                    isChannel = chanList.get(0).getType().equalsIgnoreCase("0");
                    Videos();
                }
            }
        }
    }

    public void SubCategory() {
        GridLayoutManager manager = new GridLayoutManager(activity, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                switch (subcatAdapter.getItemViewType(i)) {
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
        binding.rvCats.setLayoutManager(manager);
        binding.rvCats.addItemDecoration(new ItemOffsetDecoration(activity, R.dimen.item_off_ten));

        subcatAdapter = new SubcatAdapter(activity, subList, new OnSubcatListener() {
            @Override
            public void onItemClick(int position, Subcategory item) {
                new InterAds().showInterAds(activity, new InterAds.OnAdClosedListener() {
                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(activity, ChannelActivity.class)
                                .putExtra("catId", item.getId()));
                    }
                });
            }
        });

        binding.rvCats.setAdapter(subcatAdapter);
        binding.rvCats.getRecycledViewPool().setMaxRecycledViews(Constant.AD_TYPE, 50);
        checkData();
    }

   /* public void refreshSubcats() {
        if (binding.rvCats.getAdapter().getItemCount() <= 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    subcatAdapter.refreshAdapter(viewModel.getAllSubByCategory(mParam1));
                    binding.includedProgress.progress.setVisibility(View.GONE);
                    checkData();
                }
            }, 500);
        }
    }*/


    public void Channels() {
        GridLayoutManager manager = new GridLayoutManager(activity, 2);
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
        binding.rvCats.setLayoutManager(manager);
        binding.rvCats.addItemDecoration(new ItemOffsetDecoration(activity, R.dimen.item_off_ten));

        channelAdapter = new ChannelAdapter(activity, chanList, new OnChannelListener() {
            @Override
            public void onItemClick(int position, Channel item) {
                new InterAds().showInterAds(activity, new InterAds.OnAdClosedListener() {
                    @Override
                    public void onAdClosed() {
                        PowerPreference.getDefaultFile().putString(Constant.mChannelID, item.getYouid());
                        PowerPreference.getDefaultFile().putBoolean(Constant.mIsChannel, item.getType().equalsIgnoreCase("0"));
                        startActivity(new Intent(activity, VideoListActivity.class));
                    }
                });
            }
        });

        binding.rvCats.setAdapter(channelAdapter);
        binding.rvCats.getRecycledViewPool().setMaxRecycledViews(Constant.AD_TYPE, 50);

        checkData();
    }

/*
    public void refreshChannels() {
        if (binding.rvCats.getAdapter().getItemCount() <= 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    channelAdapter.refreshAdapter(viewModel.getAllChannelByCategory(mParam1));
                    binding.includedProgress.progress.setVisibility(View.GONE);
                    checkData();
                }
            }, 500);
        }
    }
*/

    public void Videos() {

        GridLayoutManager manager = new GridLayoutManager(activity, 2);
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
        binding.rvCats.setLayoutManager(manager);
        binding.rvCats.addItemDecoration(new ItemOffsetDecoration(activity, R.dimen.item_off_ten));

        videosAdapter = new VideosAdapter(activity, videoList, binding.rvCats, new OnVideoListener() {
            @Override
            public void onItemClick(int position, SaveEntity item) {
                new InterAds().showInterAds(activity, new InterAds.OnAdClosedListener() {
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
                        startActivity(new Intent(activity, PreviewActivity.class).putExtra(Constant.POSITION, pos));
                    }
                });
            }
        });

        binding.rvCats.setAdapter(videosAdapter);
        binding.rvCats.getRecycledViewPool().setMaxRecycledViews(Constant.AD_TYPE, 50);
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
                        if (!PowerPreference.getDefaultFile().getString(channelId, "").equalsIgnoreCase("")) {
                            if (isChannel) {
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
                PowerPreference.getDefaultFile().putString(channelId, "");
                if (isChannel) {
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

    public void checkData() {

        binding.includedProgress.progress.setVisibility(View.GONE);
        if (binding.rvCats.getAdapter() != null && binding.rvCats.getAdapter().getItemCount() > 0) {
            binding.includedProgress.llError.setVisibility(View.GONE);
        } else {
            binding.includedProgress.llError.setVisibility(View.VISIBLE);
        }
    }

    public boolean checkInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

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

            RetroClient.getInstance().getYouApi().channelApi(PowerPreference.getDefaultFile().getString(Constant.mKeyId), channelId, PowerPreference.getDefaultFile().getString(channelId, "")).enqueue(new Callback<JsonObject>() {
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
                                    PowerPreference.getDefaultFile().putString(channelId, channelResponse.nextPageToken);
                                } else {
                                    PowerPreference.getDefaultFile().putString(channelId, "");
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
                                    Constant.showToast(activity, "Something went Wrong");
                                    channelError("Something went Wrong");
                                }
                            }
                        } else {

                            if (response.code() == 403) {
                                updateAPI();
                            } else {
                                Constant.showToast(activity, "Something went Wrong");
                                channelError("Something went Wrong");
                            }

                        }

                    } catch (Exception e) {
                        Constant.showLog(e.toString());
                        e.printStackTrace();
                        Constant.showToast(activity, "Something went Wrong");
                        channelError("Something went Wrong");
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    if (t.getMessage() != null)
                        Log.e("TAG", "error" + t.getMessage());
                    channelError("Something went Wrong");
                }
            });
        } else {
            channelError(activity.getResources().getString(R.string.error_internet));
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
            RetroClient.getInstance().getYouApi().playlistApi(PowerPreference.getDefaultFile().getString(Constant.mKeyId), channelId, PowerPreference.getDefaultFile().getString(channelId, "")).enqueue(new Callback<JsonObject>() {
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
                                    PowerPreference.getDefaultFile().putString(channelId, playlistResponse.nextPageToken);
                                } else {
                                    PowerPreference.getDefaultFile().putString(channelId, "");
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
                                    Constant.showToast(activity, "Something went Wrong");
                                    playlistError("Something went Wrong");
                                }
                            }
                        } else {
                            if (response.code() == 403) {
                                updateAPI();
                            } else {
                                Constant.showToast(activity, "Something went Wrong");
                                playlistError("Something went Wrong");
                            }
                        }
                    } catch (Exception e) {
                        Constant.showLog(e.toString());
                        e.printStackTrace();
                        Constant.showToast(activity, "Something went Wrong");
                        playlistError("Something went Wrong");
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    if (t.getMessage() != null)
                        Log.e("TAG", "error" + t.getMessage());
                    playlistError("Something went Wrong");
                }
            });
        } else {
            playlistError(activity.getResources().getString(R.string.error_internet));
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
                if (isChannel) {
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
            int VERSION = 0;
            @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
            String token = PowerPreference.getDefaultFile().getString(Constant.Token, "");

            PackageManager manager = activity.getPackageManager();
            PackageInfo info = null;

            try {
                info = manager.getPackageInfo(activity.getPackageName(), 0);
                VERSION = info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Constant.showLog(e.toString());
                VERSION = BuildConfig.VERSION_CODE;
            }

            PowerPreference.getDefaultFile().putBoolean(Constant.mIsApi, true);
            RetroClient.getInstance().getApi().refreshApi(DecryptEncrypt.EncryptStr(deviceId), DecryptEncrypt.EncryptStr(token), DecryptEncrypt.EncryptStr(activity.getPackageName()), VERSION,"refresh")
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                PowerPreference.getDefaultFile().putBoolean(Constant.mIsApi, false);
                                final UpdateResponse updateResponse = new GsonBuilder().create().fromJson((DecryptEncrypt.DecryptStr(response.body())), UpdateResponse.class);

                                if(updateResponse.getFlag()) {
                                    AppInfo appInfo = updateResponse.getData().getAppInfo().get(0);
                                    PowerPreference.getDefaultFile().putString(Constant.mKeyId, appInfo.getApiKey());

                                    if (isChannel) {
                                        channelAPI();
                                    } else {
                                        playlistAPI();
                                    }
                                }else{
                                    Constant.showToast(activity, "Something went Wrong");
                                }
                            } catch (Exception e) {
                                Constant.showLog(e.toString());
                                e.printStackTrace();
                                Constant.showToast(activity, "Something went Wrong");
                                updateError("Something went Wrong");
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (t.getMessage() != null)
                                Log.e("TAG", "error" + t.getMessage());
                            updateError("Something went Wrong");
                        }
                    });
        } else {
            updateError(activity.getResources().getString(R.string.error_internet));
        }

    }
}