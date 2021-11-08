package com.example.mysocialandroidapp2.repository

import androidx.paging.PagingData
import com.example.mysocialandroidapp2.dto.Post
import kotlinx.coroutines.flow.Flow



interface WallRepository {

    val data: Flow<PagingData<Post>>
    suspend fun getAllPosts(userId: Long)
    suspend fun likeById(userId: Long, postId: Long)
}