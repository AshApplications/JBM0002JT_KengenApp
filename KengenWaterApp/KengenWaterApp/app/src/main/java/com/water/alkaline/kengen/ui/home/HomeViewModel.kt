package com.water.alkaline.kengen.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.water.alkaline.kengen.model.NetworkResult
import com.water.alkaline.kengen.repo.HomeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepo: HomeRepo) : ViewModel() {

    val updateData: LiveData<NetworkResult<ResponseBody>>
        get() = homeRepo.updateData

    val videoData: LiveData<NetworkResult<ResponseBody>>
        get() = homeRepo.videoData

    fun fetchUpdateData(requestBody: String) {
        viewModelScope.launch(Dispatchers.IO) {
            homeRepo.fetchUpdateData(requestBody)
        }
    }


    fun fetchData(
        mKey: String,
        mChannelId: String,
        pageToken: String,
        isChannel: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isChannel) {
                homeRepo.fetchChannelData(mKey, mChannelId, pageToken)
            } else {
                homeRepo.fetchPlayData(mKey, mChannelId, pageToken)
            }
        }
    }
}