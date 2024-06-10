package com.dicoding.picodiploma.loginwithanimation.data.di

import android.content.Context
import com.dicoding.picodiploma.loginwithanimation.data.repositories.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.data.repositories.MapsRepository
import com.dicoding.picodiploma.loginwithanimation.data.repositories.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.StoryConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService, pref)
    }

    //tambahkan kondisi untuk menjalankan saja ketika token tersedia
    fun provideStoryRepository(context: Context): StoryRepository? {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        return if (user.token.isNotBlank()) {

            val storyService = StoryConfig.getStoryService(user.token)
            StoryRepository.getInstance(storyService, pref)
        } else {
            null
        }
    }

    fun provideMapsRepository(context: Context): MapsRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return MapsRepository.getInstance(pref)
    }
}