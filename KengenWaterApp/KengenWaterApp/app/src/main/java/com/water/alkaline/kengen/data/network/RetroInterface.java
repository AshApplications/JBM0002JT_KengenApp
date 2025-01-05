package com.water.alkaline.kengen.data.network;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetroInterface {

    @FormUrlEncoded
    @POST("update.php")
    Call<ResponseBody> updateApi(@Field("data") String requestBody);

    @FormUrlEncoded
    @POST("feedApi.php")
    Call<ResponseBody> GetfeedApi(@Field("data") String requestBody);

    @FormUrlEncoded
    @POST("feedApi.php")
    Call<ResponseBody> SendfeedApi(@Field("data") String requestBody);




    @GET("youtube/v3/search?part=snippet&maxResults=50&order=date&type=video")
    Call<JsonObject> channelApi(@Query("key") String mkey, @Query("channelId") String mchannelId, @Query("pageToken") String pageToken);

    @GET("youtube/v3/playlistItems?part=snippet&maxResults=50")
    Call<JsonObject> playlistApi(@Query("key") String mkey, @Query("playlistId") String mplayId, @Query("pageToken") String pageToken);

}
