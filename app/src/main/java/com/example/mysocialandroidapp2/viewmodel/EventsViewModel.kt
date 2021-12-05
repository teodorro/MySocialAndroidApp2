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
import com.example.mysocialandroidapp2.dto.Coordinates
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


private val emptyEvent = Event(
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

    private val _edited = MutableLiveData(emptyEvent)
    val edited: LiveData<Event>
        get() = _edited

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

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
            _eventCreated.value = Unit
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
        _edited.value = emptyEvent
        _photo.value = noPhoto
    }

    fun edit(event: Event) {
        _edited.value = event
    }

    fun changeContent(content: String,
                      position: String,
                      date: String,
                      link: String,) {
        val content = content.trim()
        val position = position.trim()
        val date = date.trim()
        val link = if (link.isNullOrBlank()) null else link.trim()

        if (_edited.value?.content == content
            && _edited.value?.coords.toString() == position
            && _edited.value?.datetime == date
            && _edited.value?.link == link
        ) {
            return
        }

        val coordsStr = position.trim().split(" ")
        val coordsLong = coordsStr.map { x ->
            try{
                x.trim().toDouble()
            } catch (ex: Exception ){
                0.0
            }}
        val coords = Coordinates(
            if (coordsLong.count() > 0) coordsLong[0] else 0.0,
            if (coordsLong.count() > 1) coordsLong[1] else 0.0)

        _edited.value = edited.value?.copy(
            content = content,
            coords = coords,
            datetime = date,
            link = link,
            author = appAuth.userFlow.value.name,
            authorId = appAuth.userFlow.value.id
        )
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                _dataState.value = EventFeedModelState(loading = true)
                repository.likeById(id)
                _edited.value = edited.value?.copy(likedByMe = true)
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

    fun participateById(id: Long) {
        viewModelScope.launch {
            try {
                _dataState.value = EventFeedModelState(loading = true)
                repository.participateEventById(id)
                _edited.value = edited.value?.copy(participatedByMe = true)
                _dataState.value = EventFeedModelState()
            } catch (e: Exception) {
                _dataState.value = EventFeedModelState(error = true)
            }
        }
    }
}