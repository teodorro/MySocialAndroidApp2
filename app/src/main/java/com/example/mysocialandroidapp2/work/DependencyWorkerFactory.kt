package com.example.mysocialandroidapp2.work

import androidx.work.DelegatingWorkerFactory
import com.example.mysocialandroidapp2.repository.EventsRepository
import com.example.mysocialandroidapp2.repository.JobsRepository
import com.example.mysocialandroidapp2.repository.PostRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DependencyWorkerFactory @Inject constructor(
    postRepository: PostRepository,
    jobsRepository: JobsRepository,
    eventsRepository: EventsRepository,
) : DelegatingWorkerFactory() {
    init {
        addFactory(RefreshPostsWorker.Factory(postRepository))
        addFactory(SavePostWorker.Factory(postRepository))
        addFactory(RemovePostWorker.Factory(postRepository))
        addFactory(SaveJobWorker.Factory(jobsRepository))
        addFactory(RemoveJobWorker.Factory(jobsRepository))
        addFactory(RefreshEventsWorker.Factory(eventsRepository))
        addFactory(SaveEventWorker.Factory(eventsRepository))
        addFactory(RemoveEventWorker.Factory(eventsRepository))
    }
}