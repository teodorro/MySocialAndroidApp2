package com.example.mysocialandroidapp2.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mysocialandroidapp2.dao.*
import com.example.mysocialandroidapp2.entity.PostEntity
import com.example.mysocialandroidapp2.entity.PostRemoteKeyEntity
import com.example.mysocialandroidapp2.entity.PostWorkEntity
import com.example.mysocialandroidapp2.entity.UserEntity

@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class, PostWorkEntity::class, UserEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun postWorkDao(): PostWorkDao
    abstract fun userDao(): UserDao
}