package com.dicoding.storyapp.view.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.data.response.Story
import com.dicoding.storyapp.databinding.ActivityDetailBinding
import com.dicoding.storyapp.view.ViewModelFactory
import com.bumptech.glide.Glide

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private val viewModel by viewModels<DetailStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(STORY_ID)

        if(storyId != null){
            viewModel.getStoryById(storyId)
        } else{
            Toast.makeText(this, "Invalid story ID", Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.detailStory.observe(this) { story ->
            showDetailStory(story)
        }
    }

    private fun showDetailStory(story: Story?) {
        with(binding){
            Glide.with(root)
                .load(story?.photoUrl)
                .into(ivDetailPhoto)
            tvDetailName.text = story?.name
            tvDetailDescription.text = story?.description
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    companion object{
        const val STORY_ID = "story_id"

        fun start(context: Context, storyId: String){
            val intent = Intent(context, DetailStoryActivity::class.java)
            intent.putExtra(STORY_ID, storyId)
            context.startActivity(intent)
        }
    }
}