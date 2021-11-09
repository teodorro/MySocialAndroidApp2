package com.example.mysocialandroidapp2.repository

import com.example.mysocialandroidapp2.dto.Job
import kotlinx.coroutines.flow.Flow

class JobsRepositoryImpl : JobsRepository {
    override val data: Flow<List<Job>>
        get() = TODO("Not yet implemented")

    override suspend fun getJobs(userId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun save(job: Job) {
        TODO("Not yet implemented")
    }

    override suspend fun remove(jobId: Long) {
        TODO("Not yet implemented")
    }
}