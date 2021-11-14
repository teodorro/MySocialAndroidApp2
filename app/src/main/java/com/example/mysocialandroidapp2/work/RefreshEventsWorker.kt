package com.example.mysocialandroidapp2.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.mysocialandroidapp2.repository.EventsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

class RefreshEventsWorker(
    applicationContext: Context,
    params: WorkerParameters,
    private val repository: EventsRepository,
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val NAME = "com.example.work.RefreshEventsWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        try {
            repository.getAll()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
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
            RefreshEventsWorker::class.java.name ->
                RefreshEventsWorker(appContext, workerParameters, repository)
            else ->
                null
        }
    }
}