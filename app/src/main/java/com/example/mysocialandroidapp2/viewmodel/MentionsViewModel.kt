package com.example.mysocialandroidapp2.viewmodel

import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.mysocialandroidapp2.auth.AppAuth
import com.example.mysocialandroidapp2.dto.MediaUpload
import com.example.mysocialandroidapp2.dto.Post
import com.example.mysocialandroidapp2.model.FeedModelState
import com.example.mysocialandroidapp2.model.PhotoModel
import com.example.mysocialandroidapp2.repository.PostRepository
import com.example.mysocialandroidapp2.repository.UsersRepository
import com.example.mysocialandroidapp2.util.SingleLiveEvent
import com.example.mysocialandroidapp2.work.SavePostWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

private val noPhoto = PhotoModel()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MentionsViewModel @Inject constructor(
    private val postsRepository: PostRepository,
    private val workManager: WorkManager,
    usersRepository: UsersRepository,
    override val appAuth: AppAuth
) : UsersViewModel(usersRepository, appAuth) {

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _edited = MutableLiveData(emptyPost)
    val edited: LiveData<Post>
        get() = _edited

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    fun mention(userId: Long){
        val post = edited.value
        if (post != null) {
            var mentions = post.mentionIds
            if (mentions?.contains(userId))
                mentions?.remove(userId)
            else
                mentions?.add(userId)
            save()
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    val id = postsRepository.saveWork(
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
        _edited.value = emptyPost
        _photo.value = noPhoto
    }
}