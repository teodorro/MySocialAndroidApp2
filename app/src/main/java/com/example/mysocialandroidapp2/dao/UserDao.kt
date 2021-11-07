package com.example.mysocialandroidapp2.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.TypeConverter
import com.example.mysocialandroidapp2.entity.UserEntity
import com.example.mysocialandroidapp2.enumeration.AttachmentType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import java.lang.reflect.Type

@Dao
interface UserDao {
    @Query("SELECT * FROM UserEntity ORDER BY name")
    fun getAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM UserEntity WHERE id = :id")
    fun geBytId(id: Long): UserEntity
}
