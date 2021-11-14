package com.example.mysocialandroidapp2.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mysocialandroidapp2.entity.EventWorkEntity

@Dao
interface EventWorkDao {
    @Query("SELECT * FROM EventWorkEntity WHERE id = :id")
    suspend fun getById(id: Long): EventWorkEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(work: EventWorkEntity): Long

    @Query("DELETE FROM EventWorkEntity WHERE id = :id")
    suspend fun removeById(id: Long)
}