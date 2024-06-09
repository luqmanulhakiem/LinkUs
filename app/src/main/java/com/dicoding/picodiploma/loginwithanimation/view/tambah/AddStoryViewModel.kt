package com.dicoding.picodiploma.loginwithanimation.view.tambah

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.repositories.StoryRepository
import java.io.File

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun uploadStory(file: File, description: String) =
        storyRepository.uploadImage(file, description)
}