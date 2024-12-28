package com.dicoding.storyapp.view.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.database.StoryRepository
import com.dicoding.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class HomeViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _listStory = MutableLiveData<PagingData<ListStoryItem>>()
    val listStory: LiveData<PagingData<ListStoryItem>> = _listStory

    fun getStoriesWithToken() {
        viewModelScope.launch {
            storyRepository.getSession().collect {
                fetchListStory()
            }
        }
    }

    private fun fetchListStory() {
        _isLoading.value = true
        viewModelScope.launch {
            storyRepository.getStories().observeForever { pagingData ->
                _listStory.value = pagingData
                _isLoading.value = false
            }
        }
    }
}