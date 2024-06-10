package com.dicoding.picodiploma.loginwithanimation.data.retrofit

import com.dicoding.picodiploma.loginwithanimation.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig private constructor() {

    companion object {
        @Volatile
        private var apiServiceInstance: ApiService? = null
        @Volatile
        private var authServiceInstance: AuthService? = null
        @Volatile
        private var currentToken: String? = null

        fun updateToken(token: String) {
            currentToken = token
            apiServiceInstance = null // Force re-initialization
            authServiceInstance = null // Force re-initialization
        }

        fun getApiService(): ApiService {
            val token = currentToken ?: throw IllegalStateException("Token is not set")
            return apiServiceInstance ?: synchronized(this) {
                apiServiceInstance ?: buildService(ApiService::class.java, token).also {
                    apiServiceInstance = it
                }
            }
        }

        fun getAuthService(): AuthService {
            val token = currentToken ?: throw IllegalStateException("Token is not set")
            return authServiceInstance ?: synchronized(this) {
                authServiceInstance ?: buildService(AuthService::class.java, token).also {
                    authServiceInstance = it
                }
            }
        }

        private fun <T> buildService(serviceClass: Class<T>, token: String): T {
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
            return retrofit.create(serviceClass)
        }
    }
}