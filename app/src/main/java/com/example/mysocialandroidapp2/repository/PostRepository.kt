package com.example.mysocialandroidapp2.repository

import androidx.paging.PagingData
import com.example.mysocialandroidapp2.dto.Media
import com.example.mysocialandroidapp2.dto.MediaUpload
import com.example.mysocialandroidapp2.dto.Post
import com.example.mysocialandroidapp2.dto.User
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val data: Flow<PagingData<Post>>
    fun getNewerCount(postId: Long): Flow<Int>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun removeById(postId: Long)
    suspend fun likeById(postId: Long)
    suspend fun updateWasSeen()
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun saveWork(post: Post, upload: MediaUpload?): Long
    suspend fun processWork(postId: Long)
    suspend fun removeWork(postId: Long)
    suspend fun clearLocalTable()
    val allUsers: Flow<List<User>>
    suspend fun getUsers()
}