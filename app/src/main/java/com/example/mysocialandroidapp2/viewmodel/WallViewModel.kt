package com.example.mysocialandroidapp2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.work.WorkManager
import com.example.mysocialandroidapp2.auth.AppAuth
import com.example.mysocialandroidapp2.dto.Post
import com.example.mysocialandroidapp2.model.FeedModelState
import com.example.mysocialandroidapp2.repository.PostRepository
import com.example.mysocialandroidapp2.repository.WallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WallViewModel @Inject constructor(
    private val repository: WallRepository,
    private val workManager: WorkManager,
    private val appAuth: AppAuth
) : ViewModel() {

    var userId: Long = 0
    set(value) {
        field = value
        viewModelScope.launch {
            repository.clearLocalTable()
        }
        repository.userId = value
        viewModelScope.launch {
            repository.getAllPosts(userId)
        }
    }

    private val cached = repository
        .data
        .cachedIn(viewModelScope)

//    val data: Flow<PagingData<Post>> = appAuth.authStateFlow
//        .flatMapLatest { (myId, _) ->
//            cached.map { pagingData ->
//                pagingData.map { post ->
//                    //TODO
//                    post.copy()//ownedByMe = post.authorId == myId)
//                }
//            }
//        }
    val data: Flow<PagingData<Post>> = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { post ->
                    //TODO
                    post.copy()//ownedByMe = post.authorId == myId)
                }
            }
        }


    init {
        loadPosts()
    }


    fun loadPosts() = viewModelScope.launch {
        try {
//            _dataState.value = FeedModelState(loading = true)
            repository.getAllPosts(userId)
//            repository.updateWasSeen()
//            _dataState.value = FeedModelState()
        } catch (e: Exception) {
//            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
//            _dataState.value = FeedModelState(refreshing = true)
            repository.getAllPosts(userId)
//            repository.updateWasSeen()
//            _dataState.value = FeedModelState()
        } catch (e: Exception) {
//            _dataState.value = FeedModelState(error = true)
        }
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
//                _dataState.value = FeedModelState(loading = true)
                repository.likeById(userId, id)
//                edited.value = edited.value?.copy(likedByMe = true)
//                _dataState.value = FeedModelState()
            } catch (e: Exception) {
//                _dataState.value = FeedModelState(error = true)
            }
        }
    }
}