package com.example.mysocialandroidapp2.repository

import com.example.mysocialandroidapp2.dto.User
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    val data: Flow<List<User>>
    suspend fun getUsers()
}