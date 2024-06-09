package com.dicoding.picodiploma.loginwithanimation.data.repositories

import androidx.lifecycle.liveData
import com.dicoding.picodiploma.loginwithanimation.data.pref.ResultValue
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.response.AddStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.DetailStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.GetStoriesResponse
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
){
    fun getStories() = liveData {
        emit(ResultValue.Loading)
        try {
            val user = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val successGetStories = apiService.getStories()
            emit(ResultValue.Success(successGetStories))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, GetStoriesResponse::class.java)
            errorBody?.message?.let { ResultValue.Error(it) }?.let { emit(it) }
        }
    }

    fun detailStory(id: String) = liveData {
        emit(ResultValue.Loading)
        try {
            val user = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val successDetailStory = apiService.detailStory(id)
            emit(ResultValue.Success(successDetailStory))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, DetailStoryResponse::class.java)
            errorBody?.message?.let { ResultValue.Error(it) }?.let { emit(it) }
        }
    }

    fun uploadImage(imageFile: File, description: String) = liveData {
        emit(ResultValue.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val user = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val successUploadStory = apiService.uploadStory(multipartBody, requestBody)
            emit(ResultValue.Success(successUploadStory))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, AddStoryResponse::class.java)
            errorBody?.message?.let { ResultValue.Error(it) }?.let { emit(it) }
        }
    }


    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference)
            }.also { instance = it }
    }
}