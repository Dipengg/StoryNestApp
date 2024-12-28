package com.dicoding.storyapp.data.database

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storyapp.data.response.AddStoryResponse
import com.dicoding.storyapp.data.api.ApiService
import com.dicoding.storyapp.data.response.DetailStoryResponse
import com.dicoding.storyapp.data.response.LoginResponse
import com.dicoding.storyapp.data.response.RegisterResponse
import com.dicoding.storyapp.data.response.StoryResponse
import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.utils.parseErrorMessage
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryRepository private constructor(
    private val database: StoryDatabase,
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return try {
            val response = apiService.register(name, email, password)
            if (response.error == false) {
                response
            } else {
                throw Exception(response.message ?: "An unknown error occurred")
            }
        } catch (e: HttpException) {
            val errorMessage = parseErrorMessage(e)
            throw Exception(errorMessage)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(database, apiService),
            pagingSourceFactory = {
                database.storyDao().getAllStory()
            }
        ).liveData
    }

    suspend fun addStory(
        description: RequestBody,
        image: MultipartBody.Part,
        lat: Double?,
        lon: Double?
    ): AddStoryResponse {
        return try {
            apiService.addStory(description, image, lat, lon)
        } catch (e: HttpException) {
            val errorMessage = parseErrorMessage(e)
            throw Exception(errorMessage)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getDetailStory(id: String): DetailStoryResponse {
        return try{
            apiService.getDetailStory(id)
        } catch (e: HttpException) {
            val errorMessage = parseErrorMessage(e)
            throw Exception(errorMessage)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getStoriesWithLocation(location: Int = 1): StoryResponse {
        return try {
            apiService.getStoriesWithLocation(location)
        } catch (e: HttpException) {
            val errorMessage = parseErrorMessage(e)
            throw Exception(errorMessage)
        } catch (e: Exception) {
            throw e
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            database: StoryDatabase,
            userPreference: UserPreference,
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository( database, userPreference, apiService)
            }.also { instance = it }
    }
}