package com.dicoding.storyapp.view.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.database.StoryRepository
import com.dicoding.storyapp.data.response.AddStoryResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _uploadResult = MutableLiveData<Result<AddStoryResponse>>()
    val uploadResult: LiveData<Result<AddStoryResponse>> = _uploadResult

    fun uploadStory(
        description: RequestBody,
        imageMultipart: MultipartBody.Part,
        lat: Double? = null,
        lon: Double? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                storyRepository.getSession().collect {
                    try {
                        val response = storyRepository.addStory(
                            description = description,
                            image = imageMultipart,
                            lat = lat,
                            lon = lon
                        )
                        _uploadResult.value = Result.success(response)
                    } catch (e: Exception) {
                        _uploadResult.value = Result.failure(e)
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}