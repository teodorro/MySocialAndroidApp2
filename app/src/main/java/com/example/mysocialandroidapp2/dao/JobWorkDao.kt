package com.example.mysocialandroidapp2.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mysocialandroidapp2.entity.JobWorkEntity

@Dao
interface JobWorkDao {
    @Query("SELECT * FROM JobWorkEntity WHERE id = :id")
    suspend fun getById(id: Long): JobWorkEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(work: JobWorkEntity): Long

    @Query("DELETE FROM JobWorkEntity WHERE id = :id")
    suspend fun removeById(id: Long)
}