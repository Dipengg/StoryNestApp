package com.dicoding.storyapp.view.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.database.StoryRepository
import com.dicoding.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MapsViewModel (
    private val storyRepository: StoryRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: MutableLiveData<List<ListStoryItem>> = _listStory

    fun getStoriesWithToken() {
        viewModelScope.launch {
            storyRepository.getSession().collect {
                fetchListStory()
            }
        }
    }

    private fun fetchListStory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = storyRepository.getStoriesWithLocation()
                _listStory.value = response.listStory?.filterNotNull() ?: emptyList()
            } catch (e: Exception) {
                _listStory.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}