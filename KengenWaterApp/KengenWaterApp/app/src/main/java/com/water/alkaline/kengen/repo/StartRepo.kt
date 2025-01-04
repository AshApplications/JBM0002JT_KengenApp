package com.water.alkaline.kengen.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.water.alkaline.kengen.api.RetroAPI
import com.water.alkaline.kengen.model.NetworkResult
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class StartRepo @Inject constructor(val retroAPI: RetroAPI) {

    private val _updateData = MutableLiveData<NetworkResult<ResponseBody>>()
    val updateData: LiveData<NetworkResult<ResponseBody>>
        get() = _updateData

    private val _mainData = MutableLiveData<NetworkResult<ResponseBody>>()
    val mainData: LiveData<NetworkResult<ResponseBody>>
        get() = _mainData

    suspend fun fetchUpdateData(requestBody: String) {
        handleResponse(retroAPI.updateApi(requestBody),_updateData)
    }

    suspend fun fetchMainData(requestBody: String) {
        handleResponse(retroAPI.dataApi(requestBody),_mainData)
    }

    private fun handleResponse(response: Response<ResponseBody>,data: MutableLiveData<NetworkResult<ResponseBody>>) {
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