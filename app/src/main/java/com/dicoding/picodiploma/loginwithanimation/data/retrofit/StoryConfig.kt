package com.dicoding.picodiploma.loginwithanimation.data.retrofit

import android.util.Log
import com.dicoding.picodiploma.loginwithanimation.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StoryConfig {
    companion object {
        fun getStoryService(token: String): StoryService {
            if (token.isNotEmpty()) {
                Log.e("TOKEN SERVICE", token)
                val baseUrl = BuildConfig.BASE_URL

                val loggingInterceptor = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                } else {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
                }
                val authInterceptor = Interceptor { chain ->
                    val req = chain.request()
                    val requestHeaders = req.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(requestHeaders)
                }
                val client = OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(authInterceptor)
                    .build()
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                return retrofit.create(StoryService::class.java)
            }else{
                Log.e("ERRRRRRRRRRRRRRROR", "TOKEN TIDAK TERSEDIA")
                throw IllegalStateException("Token tidak tersedia")
            }
        }
    }
}