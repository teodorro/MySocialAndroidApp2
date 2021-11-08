package com.example.mysocialandroidapp2.repository

import androidx.paging.PagingData
import com.example.mysocialandroidapp2.api.DataApiService
import com.example.mysocialandroidapp2.dao.UserDao
import com.example.mysocialandroidapp2.dto.User
import com.example.mysocialandroidapp2.entity.UserEntity
import com.example.mysocialandroidapp2.entity.toDto
import com.example.mysocialandroidapp2.entity.toEntity
import com.example.mysocialandroidapp2.error.ApiError
import com.example.mysocialandroidapp2.error.NetworkError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val apiService: DataApiService,
) : UsersRepository {

    override val data = userDao.getAll()
        .map(List<UserEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getUsers() {
        try {
            // получить все посты с сервера
            val response = apiService.getUsersAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            // обновить базу. Новые добавить, несовпадающие заменить.
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            userDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw com.example.mysocialandroidapp2.error.UnknownError
        }
    }
}