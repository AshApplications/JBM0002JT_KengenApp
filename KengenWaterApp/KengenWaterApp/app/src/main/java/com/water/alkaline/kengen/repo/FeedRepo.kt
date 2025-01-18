package com.water.alkaline.kengen.repo

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.water.alkaline.kengen.api.RetroAPI
import com.water.alkaline.kengen.data.network.RetroClient
import com.water.alkaline.kengen.model.NetworkResult
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class FeedRepo  @Inject constructor(@ApplicationContext val context: Context) {

    private val _sendFeedData = MutableLiveData<NetworkResult<ResponseBody>>()
    val sendFeedData: LiveData<NetworkResult<ResponseBody>>
        get() = _sendFeedData

    private val _getFeedData = MutableLiveData<NetworkResult<ResponseBody>>()
    val getFeedData: LiveData<NetworkResult<ResponseBody>>
        get() = _getFeedData

    suspend fun sendData(requestBody: String) {
        handleResponse(RetroClient.getInstance(context).api.feedApi(requestBody),_sendFeedData)
    }

    suspend fun fetchData(requestBody: String) {
        handleResponse(RetroClient.getInstance(context).api.feedApi(requestBody),_getFeedData)
    }

    private fun handleResponse(response: Response<ResponseBody>, data: MutableLiveData<NetworkResult<ResponseBody>>) {
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