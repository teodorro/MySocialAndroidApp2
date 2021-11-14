package com.example.mysocialandroidapp2.viewmodel

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.work.*
import com.example.mysocialandroidapp2.auth.AppAuth
import com.example.mysocialandroidapp2.dto.MediaUpload
import com.example.mysocialandroidapp2.dto.Post
import com.example.mysocialandroidapp2.model.FeedModelState
import com.example.mysocialandroidapp2.model.PhotoModel
import com.example.mysocialandroidapp2.repository.PostRepository
import com.example.mysocialandroidapp2.util.SingleLiveEvent
import com.example.mysocialandroidapp2.work.RemovePostWorker
import com.example.mysocialandroidapp2.work.SavePostWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import javax.inject.Inject

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorId = 0,
    authorAvatar = "",
    likedByMe = false,
//    published = Instant.now(),
    published = Instant.now().toString(),
    coords = null,
    link = null,
    mentionIds = emptySet(),
    mentionedMe = false,
    likeOwnerIds = emptySet(),
    attachment = null
)

private val noPhoto = PhotoModel()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostsViewModel @Inject constructor(
    private val repository: PostRepository,
    private val workManager: WorkManager,
    private val appAuth: AppAuth
) : ViewModel() {

    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Post>> = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { post ->
                    //TODO
                    post.copy()//ownedByMe = post.authorId == myId)
                }
            }
        }


    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    val selectedPost = MutableStateFlow<Post?>(null)

    init {
        //loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            repository.updateWasSeen()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            repository.updateWasSeen()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    val id = repository.saveWork(
                        it, _photo.value?.uri?.let { MediaUpload(it.toFile()) }
                    )
                    val data = workDataOf(SavePostWorker.POST_KEY to id)
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val request = OneTimeWorkRequestBuilder<SavePostWorker>()
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build()
                    workManager.enqueue(request)

                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
//        edited.value = edited.value?.copy(content = text)
        edited.value = edited.value?.copy(content = text, author = appAuth.userFlow.value.name, authorId = appAuth.userFlow.value.id)
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(loading = true)
                repository.likeById(id)
                edited.value = edited.value?.copy(likedByMe = true)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(loading = true)

                val data = workDataOf(RemovePostWorker.POST_KEY to id)
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val request = OneTimeWorkRequestBuilder<RemovePostWorker>()
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build()
                workManager.enqueue(request)

                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun updateWasSeen() {
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(loading = true)
                repository.updateWasSeen()
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun clearLocalTable(){
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(loading = true)
                repository.clearLocalTable()
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

}