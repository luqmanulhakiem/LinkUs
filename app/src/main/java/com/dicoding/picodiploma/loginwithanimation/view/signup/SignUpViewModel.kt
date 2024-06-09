package com.dicoding.picodiploma.loginwithanimation.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.repositories.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.ResultValue

class SignUpViewModel(private val repository: UserRepository): ViewModel() {
    fun register(name: String, email: String, password: String): LiveData<ResultValue<Any>> {
        return repository.register(name, email, password)
    }
}