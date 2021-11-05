package com.example.mysocialandroidapp2.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.mysocialandroidapp2.api.DataApiService
import com.example.mysocialandroidapp2.dao.PostDao
import com.example.mysocialandroidapp2.dao.PostRemoteKeyDao
import com.example.mysocialandroidapp2.db.AppDb
import com.example.mysocialandroidapp2.entity.PostEntity
import com.example.mysocialandroidapp2.entity.PostRemoteKeyEntity
import com.example.mysocialandroidapp2.entity.toEntity
import com.example.mysocialandroidapp2.error.ApiError

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val service: DataApiService,
    private val db: AppDb,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
) : RemoteMediator<Int, PostEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    if (state.firstItemOrNull() != null) {
                        service.getAfter(state.firstItemOrNull()!!.id, state.config.pageSize)
                    } else
                        service.getLatest(state.config.initialLoadSize)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    service.getBefore(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message(),
            )

            db.withTransaction {
                when (loadType){
                    LoadType.REFRESH -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.AFTER,
                                id = body.first().id,
                            )
                        )
                        postDao.insert(body.toEntity(false))
                    }
                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.BEFORE,
                                id = body.last().id,
                            )
                        )
                        postDao.insert(body.toEntity(false))
                    }
                }
            }
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}