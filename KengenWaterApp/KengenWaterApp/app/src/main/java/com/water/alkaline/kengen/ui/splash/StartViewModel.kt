package com.water.alkaline.kengen.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.water.alkaline.kengen.model.NetworkResult
import com.water.alkaline.kengen.repo.StartRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(private val startRepo: StartRepo) : ViewModel() {

    val updateData: LiveData<NetworkResult<ResponseBody>>
        get() = startRepo.updateData

    val mainData: LiveData<NetworkResult<ResponseBody>>
        get() = startRepo.mainData


    fun fetchUpdateData(requestBody: String) {
        viewModelScope.launch(Dispatchers.IO) {
            startRepo.fetchUpdateData(requestBody)
        }
    }

    fun fetchMainData(requestBody: String) {
        viewModelScope.launch(Dispatchers.IO) {
            startRepo.fetchMainData(requestBody)
        }
    }
}