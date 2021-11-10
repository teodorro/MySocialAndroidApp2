package com.example.mysocialandroidapp2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.mysocialandroidapp2.model.JobsFeedModel
import com.example.mysocialandroidapp2.repository.JobsRepository
import com.example.mysocialandroidapp2.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(
    private val repository: JobsRepository
) : ViewModel() {

    var userId: Long = 0
//    var jobIds: Set<Long> = emptySet()

    val data: LiveData<JobsFeedModel> = repository.data
        .map { jobs ->
            JobsFeedModel(
                jobs.filter { x -> x. },
                jobs.isEmpty()
            )
        }.asLiveData()


}