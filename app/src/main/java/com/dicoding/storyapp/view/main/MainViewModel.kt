package com.dicoding.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.database.StoryRepository
import com.dicoding.storyapp.data.pref.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}