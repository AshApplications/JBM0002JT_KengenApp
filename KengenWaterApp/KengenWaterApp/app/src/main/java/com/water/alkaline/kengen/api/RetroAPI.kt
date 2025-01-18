package com.water.alkaline.kengen.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetroAPI {

    @FormUrlEncoded
    @POST("update.php")
    suspend fun updateApi(@Field("data") requestBody: String): Response<ResponseBody>

    @FormUrlEncoded
    @POST("dataApi.php")
    suspend fun dataApi(@Field("data") requestBody: String): Response<ResponseBody>

    @FormUrlEncoded
    @POST("feedApi.php")
    suspend fun feedApi(@Field("data") requestBody: String): Response<ResponseBody>


}