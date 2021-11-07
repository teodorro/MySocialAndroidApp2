package com.example.mysocialandroidapp2.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mysocialandroidapp2.dto.Post
import java.time.Instant

@Entity
data class PostWorkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val postId: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val published: String,
//    val published: Instant,
    @Embedded
    var coords: CoordinatesEmbeddable? = null,
    val link: String?,
    var mentionIds: Set<Long> = mutableSetOf(),
    val mentionedMe: Boolean = false,
    val likeOwnerIds: Set<Long> = emptySet(),
    val likedByMe: Boolean,
    @Embedded
    var attachment: AttachmentEmbeddable?,
    var uri: String? = null,
) {
    fun toDto() = Post(postId, authorId, author, authorAvatar, content, published, likedByMe,
        coords?.toDto(), link,
        mentionIds,
        mentionedMe,
        likeOwnerIds,
        attachment?.toDto() )

    companion object {
        fun fromDto(dto: Post) =
            PostWorkEntity(0L, dto.id, dto.authorId, dto.author, dto.authorAvatar, dto.content, dto.published,
                CoordinatesEmbeddable.fromDto(dto.coords), dto.link,
                dto.mentionIds,
                dto.mentionedMe,
                dto.likeOwnerIds,
                dto.likedByMe, AttachmentEmbeddable.fromDto(dto.attachment))
    }
}