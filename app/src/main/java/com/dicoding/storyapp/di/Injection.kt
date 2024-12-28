package com.dicoding.storyapp.di

import android.content.Context
import com.dicoding.storyapp.data.database.StoryRepository
import com.dicoding.storyapp.data.api.ApiConfig
import com.dicoding.storyapp.data.database.StoryDatabase
import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(context)
        return StoryRepository.getInstance(database, pref, apiService)
    }
}