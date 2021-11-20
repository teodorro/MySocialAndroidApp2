package com.example.mysocialandroidapp2.viewmodel

import android.content.res.Resources
import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.*
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.api.DataApiService
import com.example.mysocialandroidapp2.auth.AppAuth
import com.example.mysocialandroidapp2.auth.AuthState
import com.example.mysocialandroidapp2.dto.MediaUpload
import com.example.mysocialandroidapp2.dto.User
import com.example.mysocialandroidapp2.error.ApiError
import com.example.mysocialandroidapp2.model.PhotoModel
import com.example.mysocialandroidapp2.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
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

    private val _authenticatedEvent = SingleLiveEvent<Unit>()
    val authenticatedEvent: LiveData<Unit>
        get() = _authenticatedEvent

    private val _moveToSignUpEvent = SingleLiveEvent<Unit>()
    val moveToSignUpEvent: LiveData<Unit>
        get() = _moveToSignUpEvent

    private val noPhoto = PhotoModel()

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    private val _avatarWasSelected = SingleLiveEvent<Unit>()
    val avatarWasSelected: LiveData<Unit>
        get() = _avatarWasSelected

    val user = auth.userFlow
        .asLiveData(Dispatchers.Default)


    fun signIn(login: String, password: String) =
        viewModelScope.launch {
            val responseSignIn = apiService.signIn(login, password)
            if (!responseSignIn.isSuccessful) {
                auth.setAuth(0, "")
                return@launch
            }
            val body = responseSignIn.body() ?: throw ApiError(responseSignIn.code(), responseSignIn.message())
            auth.setAuth(body.id, body.token ?: "")

            initUser(body.id)
        }

    fun initUser(userId: Long) =
        viewModelScope.launch {
            if (userId == 0L)
                return@launch
            val responseUser = apiService.getUserById(userId)
            if (responseUser.isSuccessful){
                val body = responseUser.body() ?: throw ApiError(responseUser.code(), responseUser.message())
                auth.setUser(body.id, body.login, body.name, body.avatar, body.authorities)
            }
            else{
                auth.setUser(0, "", "", null, emptyList())
            }
        }

    fun signOut() {
        auth.removeAuth()
    }

    fun signUp(login: String, password: String, name: String, uri: Uri?) =
        viewModelScope.async {
            try {
                var response: Response<AuthState> = if (uri == null)
                    apiService.signUp(login, password, name)
                else
                    signUp(login, password, name, uri)

                if (response.isSuccessful) {
                    signIn(login, password)
                }
                else
                    throw ApiError(response.code(), response.message())
            } catch (e: Exception) {
                throw ApiError(e.hashCode(), e.message.toString())
            }
        }

    private suspend fun signUp(
        login: String,
        password: String,
        name: String,
        uri: Uri
    ): Response<AuthState> {
        val loginPart = MultipartBody.Part.createFormData("login", login)
        val passwordPart = MultipartBody.Part.createFormData("pass", password)
        val namePart = MultipartBody.Part.createFormData("name", name)
        var upload = MediaUpload(uri.toFile())
        val avatarPart = MultipartBody.Part.createFormData(
            "file", upload.file.name, upload.file.asRequestBody()
        )

        return apiService.signUpWithAvatar(loginPart, passwordPart, namePart, avatarPart)
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

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
        _avatarWasSelected.value = Unit
    }

    fun validateUserData(login: String, password: String, name: String) : String {
        if (login.isBlank())
            return Resources.getSystem().getString(R.string.validation_signup_login_blank)
        if (password.isEmpty())
            return Resources.getSystem().getString(R.string.validation_signup_password_empty)
        if (name.isBlank())
            return Resources.getSystem().getString(R.string.validation_signup_name_blank)
        val isLoginUnique = checkUserLoginUnique(login)
        if (isLoginUnique.isNotEmpty())
            return isLoginUnique

        return ""
    }

    private fun checkUserLoginUnique(login: String) : String =
        runBlocking {
            val response = apiService.getUsersAll()
            if (!response.isSuccessful){
                throw ApiError(response.code(), response.message())
            }
            val users = response.body() ?: throw ApiError(response.code(), response.message())
            if (users.any { x -> x.login == login })
                return@runBlocking "Login '$login' already exists"

            return@runBlocking ""
        }
}