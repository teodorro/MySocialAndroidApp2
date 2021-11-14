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
import com.example.mysocialandroidapp2.dto.Event
import com.example.mysocialandroidapp2.dto.MediaUpload
import com.example.mysocialandroidapp2.enumeration.EventType
import com.example.mysocialandroidapp2.model.EventFeedModelState
import com.example.mysocialandroidapp2.model.PhotoModel
import com.example.mysocialandroidapp2.repository.EventsRepository
import com.example.mysocialandroidapp2.util.SingleLiveEvent
import com.example.mysocialandroidapp2.work.RemoveEventWorker
import com.example.mysocialandroidapp2.work.SaveEventWorker
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


private val empty = Event(
    id = 0,
    content = "",
    author = "",
    authorId = 0,
    authorAvatar = "",
    likedByMe = false,
    published = Instant.now().toString(),
    datetime = Instant.now().toString(),
    coords = null,
    link = null,
    speakerIds = emptySet(),
    participantsIds = emptySet(),
    participatedByMe = false,
    likeOwnerIds = emptySet(),
    attachment = null,
    type = EventType.ONLINE
)

private val noPhoto = PhotoModel()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventsViewModel @Inject constructor(
    private val repository: EventsRepository,
    private val workManager: WorkManager,
    private val appAuth: AppAuth
) : ViewModel() {

    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Event>> = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { event ->
                    //TODO
                    event.copy()//ownedByMe = post.authorId == myId)
                }
            }
        }

    private val _dataState = MutableLiveData<EventFeedModelState>()
    val dataState: LiveData<EventFeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    val selectedPost = MutableStateFlow<Event?>(null)


    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = EventFeedModelState(loading = true)
            repository.getAll()
            repository.updateWasSeen()
            _dataState.value = EventFeedModelState()
        } catch (e: Exception) {
            _dataState.value = EventFeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = EventFeedModelState(refreshing = true)
            repository.getAll()
            repository.updateWasSeen()
            _dataState.value = EventFeedModelState()
        } catch (e: Exception) {
            _dataState.value = EventFeedModelState(error = true)
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
                    val data = workDataOf(SaveEventWorker.EVENT_KEY to id)
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val request = OneTimeWorkRequestBuilder<SaveEventWorker>()
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build()
                    workManager.enqueue(request)

                    _dataState.value = EventFeedModelState()
                } catch (e: Exception) {
                    _dataState.value = EventFeedModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }

    fun edit(event: Event) {
        edited.value = event
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
                _dataState.value = EventFeedModelState(loading = true)
                repository.likeById(id)
                edited.value = edited.value?.copy(likedByMe = true)
                _dataState.value = EventFeedModelState()
            } catch (e: Exception) {
                _dataState.value = EventFeedModelState(error = true)
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                _dataState.value = EventFeedModelState(loading = true)

                val data = workDataOf(RemoveEventWorker.EVENT_KEY to id)
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val request = OneTimeWorkRequestBuilder<RemoveEventWorker>()
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build()
                workManager.enqueue(request)

                _dataState.value = EventFeedModelState()
            } catch (e: Exception) {
                _dataState.value = EventFeedModelState(error = true)
            }
        }
    }

    fun updateWasSeen() {
        viewModelScope.launch {
            try {
                _dataState.value = EventFeedModelState(loading = true)
                repository.updateWasSeen()
                _dataState.value = EventFeedModelState()
            } catch (e: Exception) {
                _dataState.value = EventFeedModelState(error = true)
            }
        }
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun clearLocalTable(){
        viewModelScope.launch {
            try {
                _dataState.value = EventFeedModelState(loading = true)
                repository.clearLocalTable()
                _dataState.value = EventFeedModelState()
            } catch (e: Exception) {
                _dataState.value = EventFeedModelState(error = true)
            }
        }
    }


}