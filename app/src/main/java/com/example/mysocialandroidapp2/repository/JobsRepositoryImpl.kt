package com.example.mysocialandroidapp2.repository

import android.util.Log
import com.example.mysocialandroidapp2.api.DataApiService
import com.example.mysocialandroidapp2.dao.JobDao
import com.example.mysocialandroidapp2.dto.Job
import com.example.mysocialandroidapp2.entity.JobEntity
import com.example.mysocialandroidapp2.entity.toDto
import com.example.mysocialandroidapp2.entity.toEntity
import com.example.mysocialandroidapp2.error.ApiError
import com.example.mysocialandroidapp2.error.NetworkError
import com.example.mysocialandroidapp2.error.UnknownError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class JobsRepositoryImpl @Inject constructor(
    private val jobDao: JobDao,
    private val apiService: DataApiService,
) : JobsRepository {

    override val data = jobDao.getAll()
        .map(List<JobEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getJobs(userId: Long) {
        try {
            // получить все работы с сервера
            val response = apiService.getJobs(userId)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            // обновить базу. Новые добавить, несовпадающие заменить.
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(job: Job) {
        try {
            val response = apiService.saveJob(job)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(JobEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun remove(jobId: Long) {
        try {
            val response = apiService.removeJobById(jobId)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            jobDao.removeById(jobId)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun processWork(jobId: Long) {
        try {
            val entity = jobDao.getById(jobId)
            var job = entity.toDto()
            save(job)

            Log.d(null, entity.id.toString())
            Log.d(null, job.id.toString())
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeWork(jobId: Long) {
        try {
            val response = apiService.removeJobById(jobId)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            jobDao.removeById(jobId)

        } catch (e: Exception) {
            throw UnknownError
        }
    }
}