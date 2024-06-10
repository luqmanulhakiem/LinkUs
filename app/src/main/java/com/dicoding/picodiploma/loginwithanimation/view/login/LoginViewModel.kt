package com.dicoding.picodiploma.loginwithanimation.view.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.repositories.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.StoryConfig
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.StoryService
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private var storyService: StoryService? = null

    fun initializeApiService(token: String) {
        Log.e("TOKEN DISINI:", "$token")
        storyService = StoryConfig.getStoryService(token)
    }
    fun login(email: String, password: String) = repository.login(email, password)


    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}