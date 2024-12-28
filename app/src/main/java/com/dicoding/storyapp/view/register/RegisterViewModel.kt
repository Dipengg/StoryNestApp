package com.dicoding.storyapp.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.database.StoryRepository
import com.dicoding.storyapp.data.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = storyRepository.register(name, email, password)
                if (response.error == false) {
                    _registerResult.value = Result.success(response)
                } else {
                    _registerResult.value = Result.failure(Exception(response.message ?: "Register failed"))
                }
            } catch (e: Exception) {
                _registerResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}