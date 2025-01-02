package com.water.alkaline.kengen.api

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface retroAPI {

    @FormUrlEncoded
    @POST("update.php")
    fun updateApi(@Field("data") requestBody: String?): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("dataApi.php")
    fun dataApi(@Field("data") requestBody: String?): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("feedApi.php")
    fun GetfeedApi(@Field("data") requestBody: String?): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("feedApi.php")
    fun SendfeedApi(@Field("data") requestBody: String?): Call<ResponseBody?>?

}