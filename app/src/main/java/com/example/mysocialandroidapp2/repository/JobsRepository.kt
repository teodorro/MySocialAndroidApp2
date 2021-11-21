package com.example.mysocialandroidapp2.repository

import com.example.mysocialandroidapp2.dto.Job
import kotlinx.coroutines.flow.Flow

interface JobsRepository {
    val data: Flow<List<Job>>
    suspend fun getJobs(userId: Long)
    suspend fun save(job: Job)
//    suspend fun remove(jobId: Long)
    suspend fun processWork(jobId: Long)
    suspend fun removeWork(jobId: Long)
}