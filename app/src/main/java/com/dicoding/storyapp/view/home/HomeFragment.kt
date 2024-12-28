package com.dicoding.storyapp.view.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.FragmentHomeBinding
import com.dicoding.storyapp.view.LoadingStateAdapter
import com.dicoding.storyapp.view.ViewModelFactory
import com.dicoding.storyapp.view.StoryAdapter
import com.dicoding.storyapp.view.detail.DetailStoryActivity

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private val adapter = StoryAdapter(::navigateToDetail)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)
        _binding = binding

        with(binding) {
            rvStory.layoutManager = LinearLayoutManager(requireContext())
            rvStory.adapter = adapter.withLoadStateHeaderAndFooter(
                header = LoadingStateAdapter { adapter.retry() },
                footer = LoadingStateAdapter { adapter.retry() }
            )
        }

        with(viewModel) {
            getStoriesWithToken()

            isLoading.observe(viewLifecycleOwner) { isLoading ->
                showLoading(isLoading)
            }
            listStory.observe(viewLifecycleOwner) {
                Log.d("HomeFragment", "List Story: $it")
                adapter.submitData(lifecycle, it)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        _binding?.progressBar?.visibility = if (isLoading) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToDetail(id: String){
        DetailStoryActivity.start(requireContext(), id)
    }
}