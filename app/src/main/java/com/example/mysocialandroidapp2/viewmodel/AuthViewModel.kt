package com.example.mysocialandroidapp2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.api.DataApiService
import com.example.mysocialandroidapp2.auth.AppAuth
import com.example.mysocialandroidapp2.auth.AuthState
import com.example.mysocialandroidapp2.error.ApiError
import com.example.mysocialandroidapp2.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: AppAuth,
    private val apiService: DataApiService
) : ViewModel() {

    val data: LiveData<AuthState> = auth.authStateFlow
        .asLiveData(Dispatchers.Default)

    val authenticated: Boolean
        get() = auth.authStateFlow.value.id != 0L

    private val _moveToAuthEvent = SingleLiveEvent<Unit>()
    val moveToAuthEvent: LiveData<Unit>
        get() = _moveToAuthEvent

    private val _signOutEvent = SingleLiveEvent<Unit>()
    val signOutEvent: LiveData<Unit>
        get() = _signOutEvent

    private val _moveToSignUpEvent = SingleLiveEvent<Unit>()
    val moveToSignUpEvent: LiveData<Unit>
        get() = _moveToSignUpEvent

    fun signIn(login: String, password: String) =
        viewModelScope.launch {
            val response = apiService.signIn(login, password)

            if (!response.isSuccessful) {
                auth.setAuth(0, "")
                return@launch
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            auth.setAuth(body.id, body.token ?: "")
        }

    fun signOut() {
        auth.removeAuth()
    }

    fun signUp(login: String, password: String, name: String) =
        viewModelScope.launch {
            try {
                val response = apiService.signUp(login, password, name)

                if (response.isSuccessful) {
                    signIn(login, password)
//                    val responseSignIn = apiService.signIn(login, password)
//
//                    if (!responseSignIn.isSuccessful) {
//                        auth.setAuth(0, "")
//                        return@launch
//                    }
//                    val body = responseSignIn.body() ?: throw ApiError(responseSignIn.code(), responseSignIn.message())
//                    auth.setAuth(body.id, body.token ?: "")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    fun signUpInvoke() {
        _moveToSignUpEvent.value = Unit
    }

    fun moveToAuthInvoke() {
        _moveToAuthEvent.value = Unit
    }

    fun signOutInvoke() {
        _signOutEvent.value = Unit
    }
}