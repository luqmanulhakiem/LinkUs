package com.dicoding.picodiploma.loginwithanimation.view.map

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.repositories.MapsRepository

class MapsViewModel(private val mapsRepository: MapsRepository) : ViewModel() {
    fun getStoriesWithLocation() = mapsRepository.getStoriesWithLocation()

}