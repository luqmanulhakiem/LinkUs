package com.dicoding.picodiploma.loginwithanimation.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.picodiploma.loginwithanimation.data.pref.ResultValue
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.response.ErrorResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.AuthService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository private constructor(
    private val authService: AuthService,
    private val userPreference: UserPreference
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
        instance = null
    }

    fun register(name: String, email: String, password: String): LiveData<ResultValue<Any>> {
        return liveData {
            emit(ResultValue.Loading)
            try {
                val successResponse = authService.register(name, email, password).message
                emit(ResultValue.Success(successResponse))
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                emit(ResultValue.Error(errorBody.message))
            }
        }
    }

    fun login(email: String, password: String) = liveData {
        emit(ResultValue.Loading)
        try {
            val successResponse = authService.login(email, password)
            emit(ResultValue.Success(successResponse))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
            errorBody?.message?.let { ResultValue.Error(it) }?.let { emit(it) }
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            authService: AuthService,
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(authService, userPreference)
            }.also { instance = it }
    }
}