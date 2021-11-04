package com.example.mysocialandroidapp2.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.mysocialandroidapp2.entity.PostEntity
import com.example.mysocialandroidapp2.enum.AttachmentType
import kotlinx.coroutines.flow.Flow

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
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)
    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name
}