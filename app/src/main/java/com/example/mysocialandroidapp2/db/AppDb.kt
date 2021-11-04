package com.example.mysocialandroidapp2.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mysocialandroidapp2.dao.Converters
import com.example.mysocialandroidapp2.dao.PostDao
import com.example.mysocialandroidapp2.dao.PostRemoteKeyDao
import com.example.mysocialandroidapp2.dao.PostWorkDao
import com.example.mysocialandroidapp2.entity.PostEntity
import com.example.mysocialandroidapp2.entity.PostRemoteKeyEntity
import com.example.mysocialandroidapp2.entity.PostWorkEntity

@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class, PostWorkEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun postWorkDao(): PostWorkDao
}