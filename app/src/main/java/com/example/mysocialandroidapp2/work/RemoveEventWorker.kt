package com.example.mysocialandroidapp2.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.mysocialandroidapp2.repository.EventsRepository
import javax.inject.Inject
import javax.inject.Singleton

class RemoveEventWorker (
    applicationContext: Context,
    params: WorkerParameters,
    private val repository: EventsRepository,
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val EVENT_KEY = "event"
    }

    override suspend fun doWork(): Result {
        val id = inputData.getLong(EVENT_KEY, 0L)
        if (id == 0L) {
            return Result.failure()
        }
        return try {
            repository.removeWork(id)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    @Singleton
    class Factory @Inject constructor(
        private val repository: EventsRepository,
    ) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker? = when (workerClassName) {
            RemoveEventWorker::class.java.name ->
                RemoveEventWorker(appContext, workerParameters, repository)
            else ->
                null
        }
    }
}