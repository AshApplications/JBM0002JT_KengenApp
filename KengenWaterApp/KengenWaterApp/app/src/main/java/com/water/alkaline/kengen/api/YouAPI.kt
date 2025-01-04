package com.water.alkaline.kengen.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YouAPI {
    @GET("youtube/v3/search?part=snippet&maxResults=50&order=date&type=video")
    fun channelApi(
        @Query("key") mkey: String,
        @Query("channelId") mchannelId: String,
        @Query("pageToken") pageToken: String
    ): Call<JsonObject?>

    @GET("youtube/v3/playlistItems?part=snippet&maxResults=50")
    fun playlistApi(
        @Query("key") mkey: String,
        @Query("playlistId") mplayId: String,
        @Query("pageToken") pageToken: String
    ): Call<JsonObject?>
}