package com.example.mysocialandroidapp2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysocialandroidapp2.api.DataApiService
import com.example.mysocialandroidapp2.auth.AppAuth
import com.example.mysocialandroidapp2.error.ApiError
import com.example.mysocialandroidapp2.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: AppAuth,
    private val apiService: DataApiService
): ViewModel()  {

    val authenticated: Boolean
        get() = auth.authStateFlow.value.id != 0L

    private val _moveToAuthEvent = SingleLiveEvent<Unit>()
    val moveToAuthEvent: LiveData<Unit>
        get() = _moveToAuthEvent

    private val _signOutEvent = SingleLiveEvent<Unit>()
    val signOutEvent: LiveData<Unit>
        get() = _signOutEvent

    private val _signUpEvent = SingleLiveEvent<Unit>()
    val signUpEvent: LiveData<Unit>
        get() = _signUpEvent

    fun signIn(login: String, password: String) = viewModelScope.launch {
        val response = apiService.signIn(login, password)

        if (!response.isSuccessful) {
            auth.setAuth(0, "")
            return@launch
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        auth.setAuth(body.id, body.token ?: "")
    }

    fun signOut(){
        auth.removeAuth()
    }

    fun signUp(){
        //TODO
        !!!
    }

    fun signUpInvoke(){
        _signUpEvent.value = Unit
    }

    fun moveToAuthInvoke(){
        _moveToAuthEvent.value = Unit
    }

    fun signOutInvoke(){
        _signOutEvent.value = Unit
    }
}