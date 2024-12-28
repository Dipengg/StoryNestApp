package com.dicoding.storyapp

import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..10) {
            val story = ListStoryItem(
                id = "story-$i",
                name = "name $i",
                description = "description $i",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-$i",
                createdAt = "2023-01-01T00:00:00Z",
                lat = null,
                lon = null
            )
            items.add(story)
        }
        return items
    }
}