package com.example.mysocialandroidapp2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mysocialandroidapp2.model.FeedModelState
import com.example.mysocialandroidapp2.model.UsersFeedModel
import com.example.mysocialandroidapp2.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UsersRepository
) : ViewModel() {

    val userIds: List<Long> = emptyList()

    val data: LiveData<UsersFeedModel> = repository.data
        .map { users ->
            UsersFeedModel(
                users.filter { x -> userIds.contains(x.id) },
                users.isEmpty()
            )
        }.asLiveData()


    init {
        loadUsers()
    }

    fun loadUsers() = viewModelScope.launch {
        try {
            repository.getUsers()
        } catch (e: Exception) {
        }
    }

}