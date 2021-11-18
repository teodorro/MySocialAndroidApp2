package com.example.mysocialandroidapp2.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mysocialandroidapp2.dto.Post
import com.example.mysocialandroidapp2.entity.PostEntity.Companion.fromDto
import java.time.Instant

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    @Embedded
    var coords: CoordinatesEmbeddable? = null,
    val link: String?,
    var mentionIds: MutableSet<Long> = mutableSetOf(),
    val mentionedMe: Boolean = false,
    val likeOwnerIds: Set<Long> = emptySet(),
    val likedByMe: Boolean,
    @Embedded
    var attachment: AttachmentEmbeddable?,
    val wasSeen: Boolean = false,
) {
    fun toDto() = Post(id, authorId, author, authorAvatar, content, published, likedByMe,
        coords?.toDto(), link,
        mentionIds,
        mentionedMe,
        likeOwnerIds,
        attachment?.toDto())

    companion object {
        fun fromDto(dto: Post, wasSeen: Boolean) =
            PostEntity(dto.id, dto.authorId, dto.author, dto.authorAvatar, dto.content, dto.published,
                CoordinatesEmbeddable.fromDto(dto.coords), dto.link,
                dto.mentionIds,
                dto.mentionedMe,
                dto.likeOwnerIds,
                dto.likedByMe, AttachmentEmbeddable.fromDto(dto.attachment),
                wasSeen)
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(wasSeen: Boolean): List<PostEntity> {
    var postEntities = mutableListOf<PostEntity>()
    for (post in this){
        postEntities.add(fromDto(post, wasSeen))
    }
    return postEntities
}