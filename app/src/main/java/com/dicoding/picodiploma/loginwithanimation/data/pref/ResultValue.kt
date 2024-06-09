package com.dicoding.picodiploma.loginwithanimation.data.pref

sealed class ResultValue<out R> private constructor()  {
    data class Success<out T>(val data: T) : ResultValue<T>()
    data class Error(val error: String) : ResultValue<Nothing>()
    data object Loading : ResultValue<Nothing>()
}