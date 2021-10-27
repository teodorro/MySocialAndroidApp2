package com.example.mysocialandroidapp2.api

import com.example.mysocialandroidapp2.auth.AuthState
import com.example.mysocialandroidapp2.dto.PushToken
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface DataApiService {

    //region AUTH

    @POST("/api/users/authentication")
    @FormUrlEncoded
    suspend fun signIn(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<AuthState>

    @POST("/api/users/registration")
    @FormUrlEncoded
    suspend fun signUp(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): Response<AuthState>

    @POST("/api/users/registration")
    @Multipart
    suspend fun signUpWithAvatar(
        @Part("login") login: MultipartBody.Part,
        @Part("pass") pass: MultipartBody.Part,
        @Part("name") name: MultipartBody.Part,
        @Part("avatar") avatar: MultipartBody.Part,
    ): Response<AuthState>

    @POST("users/push-tokens")
    suspend fun save(@Body pushToken: PushToken): Response<Unit>

    //endregion



}