package com.dicoding.picodiploma.loginwithanimation.view.detail

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.repositories.StoryRepository

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun detailStory(id: String) = storyRepository.detailStory(id)
}