package com.example.mysocialandroidapp2.viewmodel

import androidx.lifecycle.*
import androidx.work.*
import com.example.mysocialandroidapp2.auth.AppAuth
import com.example.mysocialandroidapp2.dto.Job
import com.example.mysocialandroidapp2.dto.MediaUpload
import com.example.mysocialandroidapp2.model.FeedModelState
import com.example.mysocialandroidapp2.model.JobsFeedModel
import com.example.mysocialandroidapp2.model.JobsFeedModelState
import com.example.mysocialandroidapp2.repository.JobsRepository
import com.example.mysocialandroidapp2.util.SingleLiveEvent
import com.example.mysocialandroidapp2.work.RemoveJobWorker
import com.example.mysocialandroidapp2.work.RemovePostWorker
import com.example.mysocialandroidapp2.work.SaveJobWorker
import com.example.mysocialandroidapp2.work.SavePostWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private val empty = Job(
    id = 0,
    name = "",
    position = "",
    start = 0,
    finish = null,
    link = null
)

@HiltViewModel
class JobsViewModel @Inject constructor(
    private val repository: JobsRepository,
    private val workManager: WorkManager,
    private val appAuth: AppAuth,
) : ViewModel() {

    var userId: Long = 0

    private val edited = MutableLiveData(empty)
    private val _jobCreated = SingleLiveEvent<Unit>()
    val jobCreated: LiveData<Unit>
        get() = _jobCreated

    private val _dataState = MutableLiveData<JobsFeedModelState>()
    val dataState: LiveData<JobsFeedModelState>
        get() = _dataState

    val data: LiveData<JobsFeedModel> = repository.data
        .map { jobs ->
            JobsFeedModel(
                jobs,
                jobs.isEmpty()
            )
        }.asLiveData()

    init {
        loadJobs()
    }

    fun loadJobs() = viewModelScope.launch {
        try {
            repository.getJobs(userId)
        } catch (e: Exception) {
        }
    }

    fun save() {
        edited.value?.let {
            _jobCreated.value = Unit
            viewModelScope.launch {
                try {
                    val id = repository.save(it)
                    val data = workDataOf(SaveJobWorker.JOB_KEY to id)
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val request = OneTimeWorkRequestBuilder<SaveJobWorker>()
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build()
                    workManager.enqueue(request)

                    _dataState.value = JobsFeedModelState()
                } catch (e: Exception) {
                    _dataState.value = JobsFeedModelState(error = true)
                }
            }
        }
        edited.value = empty
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                _dataState.value = JobsFeedModelState(loading = true)

                val data = workDataOf(RemoveJobWorker.JOB_KEY to id)
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val request = OneTimeWorkRequestBuilder<RemoveJobWorker>()
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build()
                workManager.enqueue(request)

                _dataState.value = JobsFeedModelState()
            } catch (e: Exception) {
                _dataState.value = JobsFeedModelState(error = true)
            }
        }
    }
}