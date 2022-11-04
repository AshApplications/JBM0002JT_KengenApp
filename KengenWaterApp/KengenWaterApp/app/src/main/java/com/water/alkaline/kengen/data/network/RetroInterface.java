package com.water.alkaline.kengen.data.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetroInterface {

    @FormUrlEncoded
    @POST("update2Api.php")
    Call<String> updateApi(@Field("device") String device,@Field("token") String token,@Field("pkgName") String pkgName,@Field("versionCode") int versionCode);

    @FormUrlEncoded
    @POST("update2Api.php")
    Call<String> refreshApi(@Field("device") String device,@Field("token") String token,@Field("pkgName") String pkgName,@Field("versionCode") int versionCode,@Field("work") String work);


    @FormUrlEncoded
    @POST("feedApi.php")
    Call<String> GetfeedApi(@Field("mtoken") String mtoken);

    @FormUrlEncoded
    @POST("feedApi.php")
    Call<String> SendfeedApi(@Field("mtoken") String mtoken,@Field("device") String device, @Field("star") String star, @Field("message") String message);


    @FormUrlEncoded
    @POST("dataApi.php")
    Call<String> dataApi(@Field("mtoken") String v);


    @GET("youtube/v3/search?part=snippet&maxResults=50&order=date&type=video")
    Call<JsonObject> channelApi(@Query("key") String mkey, @Query("channelId") String mchannelId, @Query("pageToken") String pageToken);

    @GET("youtube/v3/playlistItems?part=snippet&maxResults=50")
    Call<JsonObject> playlistApi(@Query("key") String mkey, @Query("playlistId") String mplayId, @Query("pageToken") String pageToken);

}
