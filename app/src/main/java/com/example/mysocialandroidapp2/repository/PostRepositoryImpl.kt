package com.example.mysocialandroidapp2.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.paging.*
import com.example.mysocialandroidapp2.api.DataApiService
import com.example.mysocialandroidapp2.dao.PostDao
import com.example.mysocialandroidapp2.dao.PostRemoteKeyDao
import com.example.mysocialandroidapp2.dao.PostWorkDao
import com.example.mysocialandroidapp2.db.AppDb
import com.example.mysocialandroidapp2.dto.Attachment
import com.example.mysocialandroidapp2.dto.Media
import com.example.mysocialandroidapp2.dto.MediaUpload
import com.example.mysocialandroidapp2.dto.Post
import com.example.mysocialandroidapp2.entity.PostEntity
import com.example.mysocialandroidapp2.entity.PostWorkEntity
import com.example.mysocialandroidapp2.entity.toEntity
import com.example.mysocialandroidapp2.enum.AttachmentType
import com.example.mysocialandroidapp2.error.ApiError
import com.example.mysocialandroidapp2.error.AppError
import com.example.mysocialandroidapp2.error.NetworkError
import com.example.mysocialandroidapp2.error.UnknownError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val postWorkDao: PostWorkDao,
    appDb: AppDb,
    postRemoteKeyDao: PostRemoteKeyDao,
    private val apiService: DataApiService,
) : PostRepository {
    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        remoteMediator = PostRemoteMediator(apiService, appDb, postDao, postRemoteKeyDao),
        pagingSourceFactory = postDao::pagingSource,
    ).flow.map { pagingData ->
        pagingData.map(PostEntity::toDto)
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(5_000)

            val response = apiService.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity(false))
            emit(body.size)
        }
    }.catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            // получить все посты с сервера
            val response = apiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            // обновить базу. Новые добавить, несовпадающие заменить.
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity(false))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = apiService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body, true)) //TODO: wasSeen == false
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            postDao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            val postResponse = apiService.getById(id)
            val postBody =
                postResponse.body() ?: throw ApiError(postResponse.code(), postResponse.message())

            val response: Response<Post> = if (!postBody.likedByMe) {
                apiService.likeById(id)
            } else {
                apiService.dislikeById(id)
            }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body, true)) //TODO: wasSeen = false
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun updateWasSeen() {
        try {
            postDao.updateWasSeen()
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = upload(upload)
            // TODO: add support for other types
            val postWithAttachment =
                post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
            save(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: java.io.IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )

            val response = apiService.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: java.io.IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWork(post: Post, upload: MediaUpload?): Long {
        try {
            val entity = PostWorkEntity.fromDto(post).apply {
                if (upload != null) {
                    this.uri = upload.file.toUri().toString()
                }
            }
            return postWorkDao.insert(entity)
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun processWork(id: Long) {
        try {// TODO: handle this in homework
            val entity = postWorkDao.getById(id)
            var post = entity.toDto()
            if (entity.uri != null) {
                val upload = MediaUpload(Uri.parse(entity.uri).toFile())
                val media = upload(upload)
                post = post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
            }
            save(post)

            Log.d(null, entity.id.toString())
            Log.d(null, post.id.toString())
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeWork(id: Long) {
        try {
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            postDao.removeById(id)

        } catch (e: Exception) {
            throw UnknownError
        }
    }
}