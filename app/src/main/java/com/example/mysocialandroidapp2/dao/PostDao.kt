package com.example.mysocialandroidapp2.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.mysocialandroidapp2.entity.PostEntity
import com.example.mysocialandroidapp2.enumeration.AttachmentType
import kotlinx.coroutines.flow.Flow
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.Instant


@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, PostEntity>

    @Query("DELETE FROM PostEntity")
    suspend fun removeAll()

    @Query("SELECT * FROM PostEntity WHERE wasSeen = 1 ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getById(id: Long): PostEntity

    @Query("SELECT COUNT(*) FROM PostEntity WHERE id = :id")
    suspend fun getCountOfId(id: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET wasSeen = 1 WHERE wasSeen = 0")
    suspend fun updateWasSeen()

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)
}

class Converters {
    var gson = Gson()

    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)
    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name

    @TypeConverter
    fun stringToSetLongType(data: String?): Set<Long> {
        if (data == null) {
            return emptySet()
        }
        val setType: Type = object : TypeToken<Set<Long>>() {}.type
        return gson.fromJson(data, setType)
    }

    @TypeConverter
    fun fromSetLongType(data: Set<Long>): String? {
        return gson.toJson(data)
    }

//    @TypeConverter
//    fun toInstantType(value: Long): Instant = Instant.ofEpochMilli(value)
//    @TypeConverter
//    fun fromInstantType(value: Instant) = value.toEpochMilli()

//    @TypeConverter
//    fun toLikeOwnerIds(data: String?): Set<Long> {
//        if (data == null) {
//            return emptySet()
//        }
//        val setType: Type = object : TypeToken<Set<Long>>() {}.type
//        return gson.fromJson(data, setType)
//    }
//
//    @TypeConverter
//    fun fromLikeOwnerIds(data: Set<Long>): String? {
//        return gson.toJson(data)
//    }
}