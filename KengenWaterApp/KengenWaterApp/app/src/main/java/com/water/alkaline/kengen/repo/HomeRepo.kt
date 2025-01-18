package com.water.alkaline.kengen.repo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.water.alkaline.kengen.data.network.RetroClient
import com.water.alkaline.kengen.model.NetworkResult
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class HomeRepo @Inject constructor(@ApplicationContext val context: Context) {

    private val _updateData = MutableLiveData<NetworkResult<ResponseBody>>()
    val updateData: LiveData<NetworkResult<ResponseBody>>
        get() = _updateData

    private val _videoData = MutableLiveData<NetworkResult<ResponseBody>>()
    val videoData: LiveData<NetworkResult<ResponseBody>>
        get() = _videoData


    suspend fun fetchUpdateData(requestBody: String) {
        handleResponse(RetroClient.getInstance(context).api.updateApi(requestBody), _updateData)
    }

    suspend fun fetchChannelData(
        mKey: String,
        mChannelId: String,
        pageToken: String
    ) {
        handleResponse(
            RetroClient.getInstance(context).youApi.channelApi(
                mKey,
                mChannelId,
                pageToken
            ), _videoData
        )
    }

    suspend fun fetchPlayData(
        mKey: String,
        mPlayId: String,
        pageToken: String
    ) {
        handleResponse(
            RetroClient.getInstance(context).youApi.playlistApi(
                mKey,
                mPlayId,
                pageToken
            ), _videoData
        )
    }

    private fun handleResponse(
        response: Response<ResponseBody>,
        data: MutableLiveData<NetworkResult<ResponseBody>>
    ) {
        if (response.isSuccessful && response.body() != null) {
            data.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            data.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            data.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

}