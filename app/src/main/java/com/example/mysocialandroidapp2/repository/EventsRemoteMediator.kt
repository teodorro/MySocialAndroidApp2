package com.example.mysocialandroidapp2.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.mysocialandroidapp2.api.DataApiService
import com.example.mysocialandroidapp2.dao.EventDao
import com.example.mysocialandroidapp2.dao.EventRemoteKeyDao
import com.example.mysocialandroidapp2.db.AppDb
import com.example.mysocialandroidapp2.entity.EventEntity
import com.example.mysocialandroidapp2.entity.EventRemoteKeyEntity
import com.example.mysocialandroidapp2.entity.toEntity
import com.example.mysocialandroidapp2.error.ApiError

@OptIn(ExperimentalPagingApi::class)
class EventsRemoteMediator (
    private val service: DataApiService,
    private val db: AppDb,
    private val eventDao: EventDao,
    private val eventRemoteKeyDao: EventRemoteKeyDao,
) : RemoteMediator<Int, EventEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
//                    if (state.firstItemOrNull() != null) {
//                        service.getEventsAfter(state.firstItemOrNull()!!.id, state.config.pageSize)
//                    } else
                        service.getEventsLatest(state.config.initialLoadSize)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
//                    val id = eventRemoteKeyDao.min() ?: return MediatorResult.Success(
//                        endOfPaginationReached = false
//                    )
//                    service.getEventsBefore(id, state.config.pageSize)
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
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                type = EventRemoteKeyEntity.KeyType.AFTER,
                                id = body.first().id,
                            )
                        )
                        eventDao.insert(body.toEntity())
                    }
                    LoadType.APPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                type = EventRemoteKeyEntity.KeyType.BEFORE,
                                id = body.last().id,
                            )
                        )
                        eventDao.insert(body.toEntity())
                    }
                }
            }
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}