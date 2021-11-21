package com.water.alkaline.kengen.data.network;

import com.google.gson.JsonObject;
import com.water.alkaline.kengen.model.feedback.FeedbackResponse;
import com.water.alkaline.kengen.model.main.MainResponse;
import com.water.alkaline.kengen.model.update.UpdateResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetroInterface {

    @FormUrlEncoded
    @POST("updtApi.php")
    Call<UpdateResponse> updateApi(@Field("work") String method);

    @FormUrlEncoded
    @POST("userApi.php")
    Call<FeedbackResponse> userApi(@Field("device") String device, @Field("version") int version);


    @FormUrlEncoded
    @POST("feedApi.php")
    Call<FeedbackResponse> feedApi(@Field("device") String device, @Field("star") String star, @Field("message") String message);

    @POST("dataApi.php")
    Call<MainResponse> dataApi();


    @GET("youtube/v3/search?part=snippet&maxResults=50&order=date&type=video")
    Call<JsonObject> channelApi(@Query("key") String mkey, @Query("channelId") String mchannelId, @Query("pageToken") String pageToken);

    @GET("youtube/v3/playlistItems?part=snippet&maxResults=50")
    Call<JsonObject> playlistApi(@Query("key") String mkey, @Query("playlistId") String mplayId, @Query("pageToken") String pageToken);

}
