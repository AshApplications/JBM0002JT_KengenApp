package com.water.alkaline.kengen.ui.feedback

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.water.alkaline.kengen.model.NetworkResult
import com.water.alkaline.kengen.repo.FeedRepo
import com.water.alkaline.kengen.repo.StartRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(private val feedRepo: FeedRepo) : ViewModel() {
    val sendFeedData: LiveData<NetworkResult<ResponseBody>>
        get() = feedRepo.sendFeedData

    val getFeedData: LiveData<NetworkResult<ResponseBody>>
        get() = feedRepo.getFeedData

    fun sendFeedData(requestBody: String) {
        viewModelScope.launch(Dispatchers.IO) {
            feedRepo.sendData(requestBody)
        }
    }

    fun fetchData(requestBody: String) {
        viewModelScope.launch(Dispatchers.IO) {
            feedRepo.fetchData(requestBody)
        }
    }
}