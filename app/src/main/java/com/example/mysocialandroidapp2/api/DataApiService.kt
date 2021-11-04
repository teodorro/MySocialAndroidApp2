package com.example.mysocialandroidapp2.api

import com.example.mysocialandroidapp2.auth.AuthState
import com.example.mysocialandroidapp2.dto.Post
import com.example.mysocialandroidapp2.dto.PushToken
import com.example.mysocialandroidapp2.dto.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface DataApiService {

    //region USERLIST

    @GET("users")
    suspend fun getUsersAll(): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<User>

    //endregion


    //region AUTH

    @POST("users/authentication")
    @FormUrlEncoded
    suspend fun signIn(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<AuthState>

    @POST("users/registration")
    @FormUrlEncoded
    suspend fun signUp(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): Response<AuthState>

    @POST("users/registration")
    @Multipart
    suspend fun signUpWithAvatar(
        @Part login: MultipartBody.Part,
        @Part pass: MultipartBody.Part,
        @Part name: MultipartBody.Part,
        @Part file: MultipartBody.Part,
    ): Response<AuthState>

    @POST("users/push-tokens")
    suspend fun save(@Body pushToken: PushToken): Response<Unit>

    //endregion


    //region MEDIA

    @GET("avatars/{filename}")
    suspend fun getAvatar(@Path("filename") filename: String): Response<Any>

    @GET("media/{filename}")
    suspend fun getMedia(@Path("filename") filename: String): Response<Any>

    @GET("media/{id}")
    suspend fun getMediaById(@Path("id") id: String): Response<Any>

    @Multipart
    @POST("media")
    suspend fun uploadMedia(@Part media: MultipartBody.Part): Response<Any>

    //endregion


    //region POSTS

    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    //endregion


}