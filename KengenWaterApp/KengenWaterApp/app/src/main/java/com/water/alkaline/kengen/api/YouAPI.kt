package com.water.alkaline.kengen.api

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface YouAPI {
    @GET("youtube/v3/search?part=snippet&maxResults=50&order=date&type=video")
    suspend fun channelApi(
        @Query("key") mKey: String,
        @Query("channelId") mChannelId: String,
        @Query("pageToken") pageToken: String
    ): Response<ResponseBody>

    @GET("youtube/v3/playlistItems?part=snippet&maxResults=50")
    suspend fun playlistApi(
        @Query("key") mKey: String,
        @Query("playlistId") mPlayId: String,
        @Query("pageToken") pageToken: String
    ): Response<ResponseBody>
}