package com.dicoding.storyapp.data.database

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storyapp.data.api.ApiService
import com.dicoding.storyapp.data.response.ListStoryItem

class StoryPagingSource(
    private val apiService: ApiService
): PagingSource<Int, ListStoryItem>() {

    companion object{
        const val INITIAL_PAGE_INDEX = 1
    }
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories(position, params.loadSize)

            Log.d("PagingSource", "Loaded data for page: $position, size: ${responseData.listStory?.size}")

            val stories = responseData.listStory.orEmpty().filterNotNull()
            val nextKey = if (stories.isEmpty()) null else position + 1

            LoadResult.Page(
                data = stories,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: Exception) {
            Log.e("PagingSource", "Error loading data: ${exception.localizedMessage}")
            LoadResult.Error(exception)
        }
    }
}