package com.water.alkaline.kengen.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.water.alkaline.kengen.api.RetroAPI
import com.water.alkaline.kengen.model.NetworkResult
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class FeedRepo  @Inject constructor(private val retroAPI: RetroAPI) {

    private val _sendFeedData = MutableLiveData<NetworkResult<ResponseBody>>()
    val sendFeedData: LiveData<NetworkResult<ResponseBody>>
        get() = _sendFeedData

    private val _getFeedData = MutableLiveData<NetworkResult<ResponseBody>>()
    val getFeedData: LiveData<NetworkResult<ResponseBody>>
        get() = _getFeedData

    suspend fun sendData(requestBody: String) {
        handleResponse(retroAPI.sendFeedApi(requestBody),_sendFeedData)
    }

    suspend fun fetchData(requestBody: String) {
        handleResponse(retroAPI.getFeedApi(requestBody),_getFeedData)
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