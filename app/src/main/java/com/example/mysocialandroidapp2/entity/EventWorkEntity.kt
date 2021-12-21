package com.example.mysocialandroidapp2.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mysocialandroidapp2.dto.Event
import com.example.mysocialandroidapp2.dto.Post
import com.example.mysocialandroidapp2.enumeration.EventType
import java.time.Instant

@Entity
data class EventWorkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val datetime: String? = null,
    val published: String? = null,
    @Embedded
    val coords: CoordinatesEmbeddable? = null,
    val isOnline: Boolean = false,
    val likeOwnerIds: Set<Long> = emptySet(),
    val likedByMe: Boolean = false,
    val speakerIds: MutableSet<Long> = mutableSetOf(),
    val participantsIds: Set<Long> = emptySet(),
    val participatedByMe: Boolean = false,
    @Embedded
    var attachment: AttachmentEmbeddable? = null,
    var link: String? = null,
){
    fun toDto() = Event(id, authorId, author, authorAvatar, content, datetime, published,
        coords?.toDto(),
        if (isOnline) EventType.ONLINE else EventType.OFFLINE,
        likeOwnerIds, likedByMe, speakerIds, participantsIds, participatedByMe,
        attachment?.toDto(),
        link)

    companion object {
        fun fromDto(dto: Event) =
            EventWorkEntity(dto.id, dto.authorId, dto.author, dto.authorAvatar, dto.content, dto.datetime?.toString(), dto.published,
                CoordinatesEmbeddable.fromDto(dto.coords), dto.type == EventType.ONLINE,
                dto.likeOwnerIds, dto.likedByMe, dto.speakerIds, dto.participantsIds, dto.participatedByMe,
                AttachmentEmbeddable.fromDto(dto.attachment),
                dto.link)
    }
}
