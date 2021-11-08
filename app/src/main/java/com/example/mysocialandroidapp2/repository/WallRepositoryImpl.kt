package com.example.mysocialandroidapp2.repository

import androidx.paging.*
import com.example.mysocialandroidapp2.api.DataApiService
import com.example.mysocialandroidapp2.dao.PostDao
import com.example.mysocialandroidapp2.dao.PostRemoteKeyDao
import com.example.mysocialandroidapp2.dao.PostWorkDao
import com.example.mysocialandroidapp2.db.AppDb
import com.example.mysocialandroidapp2.dto.Post
import com.example.mysocialandroidapp2.entity.PostEntity
import com.example.mysocialandroidapp2.entity.toEntity
import com.example.mysocialandroidapp2.error.ApiError
import com.example.mysocialandroidapp2.error.NetworkError
import com.example.mysocialandroidapp2.error.UnknownError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val postWorkDao: PostWorkDao,
    appDb: AppDb,
    postRemoteKeyDao: PostRemoteKeyDao,
    private val apiService: DataApiService,
    ): WallRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        remoteMediator = WallRemoteMediator(apiService, appDb, postDao, postRemoteKeyDao),
        pagingSourceFactory = postDao::pagingSource,
    ).flow.map { pagingData ->
        pagingData.map(PostEntity::toDto)
    }

    override suspend fun getAllPosts(userId: Long) {
        try {
            // получить все посты с сервера
            val response = apiService.getWallPosts(userId)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            // обновить базу. Новые добавить, несовпадающие заменить.
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity(false))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(userId: Long, postId: Long) {
        try {
            val postResponse = apiService.getWallPostById(userId, postId)
            val postBody =
                postResponse.body() ?: throw ApiError(postResponse.code(), postResponse.message())

            val response: Response<Post> = if (!postBody.likedByMe) {
                apiService.likeWallPostById(userId, postId)
            } else {
                apiService.dislikeWallPostById(userId, postId)
            }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body, true)) //TODO: wasSeen = false
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }



}