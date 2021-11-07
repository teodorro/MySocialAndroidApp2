package com.example.mysocialandroidapp2.api

import com.example.mysocialandroidapp2.auth.AuthState
import com.example.mysocialandroidapp2.dto.Media
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
    suspend fun getAvatar(@Path("filename") filename: String): Response<Media>

    @GET("media/{filename}")
    suspend fun getMedia(@Path("filename") filename: String): Response<Media>

    @GET("media/{id}")
    suspend fun getMediaById(@Path("id") id: String): Response<Media>

    @Multipart
    @POST("media")
    suspend fun uploadMedia(@Part media: MultipartBody.Part): Response<Media>

    //endregion


    //region POSTS

    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>

    @GET("posts/{id}/before")
    suspend fun getBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("posts/{id}/after")
    suspend fun getAfter(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    //endregion


    //region WALL

    @GET("my/wall/latest")
    suspend fun getMyWallPostsLatest(@Query("count") count: Int): Response<List<Post>>

    @GET("{id}/wall/latest")
    suspend fun getWallPostsLatest(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>

    @GET("{id}/wall")
    suspend fun getWallPosts(@Path("id") id: Long): Response<List<Post>>

    @GET("{id}/wall/{wall_post_id}")
    suspend fun getWallPostById(@Path("id") id: Long): Response<Post>

    @POST("{id}/wall/{wall_post_id}/likes")
    suspend fun likeWallPostById(@Path("id") id: Long, @Path("wall_post_id") wall_post_id: Long): Response<Post>

    @DELETE("{id}/wall/{wall_post_id}/likes")
    suspend fun dislikeWallPostById(@Path("id") id: Long, @Path("wall_post_id") wall_post_id: Long): Response<Post>

    //endregion





}