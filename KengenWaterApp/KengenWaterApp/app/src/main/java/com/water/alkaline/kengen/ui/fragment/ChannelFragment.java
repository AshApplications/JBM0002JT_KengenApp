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
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.BuildConfig;
import com.water.alkaline.kengen.Encrypt.DecryptEncrypt;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.data.network.RetroClient;
import com.water.alkaline.kengen.databinding.FragmentChannelBinding;
import com.water.alkaline.kengen.model.ErrorReponse;
import com.water.alkaline.kengen.model.SaveEntity;
import com.water.alkaline.kengen.model.channel.ChannelResponse;
import com.water.alkaline.kengen.model.channel.PlaylistResponse;
import com.water.alkaline.kengen.model.main.Channel;
import com.water.alkaline.kengen.model.main.Subcategory;
import com.water.alkaline.kengen.model.update.AppInfo;
import com.water.alkaline.kengen.model.update.UpdateResponse;
import com.water.alkaline.kengen.ui.activity.ChannelActivity;
import com.water.alkaline.kengen.ui.activity.HomeActivity;
import com.water.alkaline.kengen.ui.activity.PreviewActivity;
import com.water.alkaline.kengen.ui.adapter.ChannelAdapter;
import com.water.alkaline.kengen.ui.adapter.SubcatAdapter;
import com.water.alkaline.kengen.ui.adapter.VideosAdapter;
import com.water.alkaline.kengen.ui.listener.OnChannelListener;
import com.water.alkaline.kengen.ui.listener.OnLoadMoreListener;
import com.water.alkaline.kengen.ui.listener.OnSubcatListener;
import com.water.alkaline.kengen.ui.listener.OnVideoListener;
import com.water.alkaline.kengen.utils.Constant;
import com.water.alkaline.kengen.utils.uiController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChannelFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1", ARG_PARAM2 = "param2", ARG_PARAM3 = "param3";
    public Dialog dialog;
    public AppViewModel viewModel;
    String channelId;
    boolean isChannel = true;
    FragmentChannelBinding binding;
    Activity activity;

    List<Subcategory> subList = new ArrayList<>();
    List<Channel> chanList = new ArrayList<>();
    List<SaveEntity> videoList = new ArrayList<>();

    SubcatAdapter subcatAdapter;
    ChannelAdapter channelAdapter;
    VideosAdapter videosAdapter;
    private String mParam1, mParam2;

    public ChannelFragment() {
    }

    public ChannelFragment(Activity activity) {
        this.activity = activity;
    }

    public static ChannelFragment newInstance(Activity activity, String param1, String param2, boolean param3) {
        ChannelFragment fragment = new ChannelFragment(activity);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putBoolean(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        context.getPackageManager();
        if (activity == null) {
            if (context instanceof HomeActivity) {
                activity = (HomeActivity) context;
            } else if (context instanceof ChannelActivity) {
                activity = (ChannelActivity) context;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1, "");
            mParam2 = getArguments().getString(ARG_PARAM2, "");
            isChannel = getArguments().getBoolean(ARG_PARAM3, false);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanseState) {
        binding = FragmentChannelBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        if (mParam2.equalsIgnoreCase("Home")) {
            getFromSubCategory();
        } else if (mParam2.equalsIgnoreCase("Channel")) {
            getFromChannel();
        } else if (mParam2.equalsIgnoreCase("Video")) {
            getFromVideo();
        }
    }

    public void getFromSubCategory() {
        if (activity != null) {
            subList = viewModel.getAllSubByCategory(mParam1);
            if (subList.size() > 1) {
                SubCategory();
            } else if (subList.size() == 1) {
                chanList = viewModel.getAllChannelByCategory(subList.get(0).getId());
                if (chanList.size() > 1) {
                    Channels();
                } else if (chanList.size() == 1) {
                    channelId = chanList.get(0).getYouid();
                    isChannel = chanList.get(0).getType().equalsIgnoreCase("0");
                    Videos();
                }
            }
        }
    }

    public void getFromChannel() {
        if (activity != null) {
            chanList = viewModel.getAllChannelByCategory(mParam1);
            if (chanList.size() > 1) {
                Channels();
            } else if (chanList.size() == 1) {
                channelId = chanList.get(0).getYouid();
                isChannel = chanList.get(0).getType().equalsIgnoreCase("0");
                Videos();
            }
        }
    }

    public void getFromVideo() {
        if (activity != null) {
            channelId = mParam1;
            Videos();
        }
    }

    public void refreshFragment() {
        if (!subList.isEmpty() && subcatAdapter != null) {
            subcatAdapter.refreshAdapter(subList);
        } else if (!chanList.isEmpty() && channelAdapter != null) {
            channelAdapter.refreshAdapter(chanList);
        } else if (!videoList.isEmpty() && videosAdapter != null) {
            videosAdapter.refreshAdapter(videoList);
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
        subcatAdapter = new SubcatAdapter(activity, subList, new OnSubcatListener() {
            @Override
            public void onItemClick(int position, Subcategory item) {
               uiController.gotoIntent(activity, new Intent(activity, ChannelActivity.class).putExtra("catId", item.getId()), true, false);
            }
        });

        binding.rvCats.setAdapter(subcatAdapter);
        binding.rvCats.setItemViewCacheSize(100);
        checkData();
    }

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
        channelAdapter = new ChannelAdapter(activity, chanList, new OnChannelListener() {
            @Override
            public void onItemClick(int position, Channel item) {
                uiController.gotoIntent(activity, new Intent(activity, ChannelActivity.class).putExtra("catId", item.getYouid()).putExtra("isChannel", item.getType().equalsIgnoreCase("0")), true, false);
            }
        });
        binding.rvCats.setAdapter(channelAdapter);
        binding.rvCats.setItemViewCacheSize(100);

        checkData();
    }

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
        videosAdapter = new VideosAdapter(activity, videoList, binding.rvCats, new OnVideoListener() {
            @Override
            public void onItemClick(int position, SaveEntity item) {
                videoList.removeAll(Collections.singleton(null));
                int pos = position;
                for (int i = 0; i < videoList.size(); i++) {
                    if (videoList.get(i).videoId.equalsIgnoreCase(item.videoId)) {
                        pos = i;
                        break;
                    }
                }
                PowerPreference.getDefaultFile().putString(Constant.mList, new Gson().toJson(videoList));
                uiController.gotoIntent(activity, new Intent(activity, PreviewActivity.class).putExtra(Constant.POSITION, pos), true, false);
            }
        });

        binding.rvCats.setAdapter(videosAdapter);
        binding.rvCats.setItemViewCacheSize(100);
        videosAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                try {
                    videosAdapter.arrayList.add(new SaveEntity("99999", null, null, null));
                    videosAdapter.notifyItemInserted(videosAdapter.arrayList.size() - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Handler().postDelayed(() -> {
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
                }, 2000);
            }
        });

        new Handler().postDelayed(() -> {
            PowerPreference.getDefaultFile().putString(channelId, "");
            if (isChannel) {
                channelAPI();
            } else {
                playlistAPI();
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
            RetroClient.getInstance(activity).getYouApi().channelApi(PowerPreference.getDefaultFile().getString(Constant.mKeyId), channelId, PowerPreference.getDefaultFile().getString(channelId, "")).enqueue(new Callback<JsonObject>() {
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
            channelError(activity.getResources().getString(R.string.kk_error_no_internet));
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
            RetroClient.getInstance(activity).getYouApi().playlistApi(PowerPreference.getDefaultFile().getString(Constant.mKeyId), channelId, PowerPreference.getDefaultFile().getString(channelId, "")).enqueue(new Callback<JsonObject>() {
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
            playlistError(activity.getResources().getString(R.string.kk_error_no_internet));
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
            String token = PowerPreference.getDefaultFile().getString(Constant.Token, "123abc");

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
            RetroClient.getInstance(activity).getApi().updateApi(DecryptEncrypt.EncryptStr(activity, MyApplication.updateApi(activity, deviceId, token, activity.getPackageName(), String.valueOf(VERSION), "refresh")))
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                PowerPreference.getDefaultFile().putBoolean(Constant.mIsApi, false);
                                final UpdateResponse updateResponse = new GsonBuilder().create().fromJson((DecryptEncrypt.DecryptStr(activity, response.body().string())), UpdateResponse.class);

                                if (updateResponse.getFlag()) {
                                    AppInfo appInfo = updateResponse.getData().getAppInfo().get(0);
                                    PowerPreference.getDefaultFile().putString(Constant.mKeyId, appInfo.getApiKey());

                                    if (isChannel) {
                                        channelAPI();
                                    } else {
                                        playlistAPI();
                                    }
                                } else {
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
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            if (t.getMessage() != null)
                                Log.e("TAG", "error" + t.getMessage());
                            updateError("Something went Wrong");
                        }
                    });
        } else {
            updateError(activity.getResources().getString(R.string.kk_error_no_internet));
        }

    }
}