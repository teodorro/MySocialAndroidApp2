package com.example.mysocialandroidapp2.api

import com.example.mysocialandroidapp2.auth.AuthState
import com.example.mysocialandroidapp2.dto.PushToken
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface DataApiService {

    @POST("/api/users/authentication")
    @FormUrlEncoded
    suspend fun signIn(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<AuthState>

    @POST("users/push-tokens")
    suspend fun save(@Body pushToken: PushToken): Response<Unit>
}