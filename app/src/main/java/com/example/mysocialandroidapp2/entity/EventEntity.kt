package com.example.mysocialandroidapp2.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mysocialandroidapp2.dto.Event
import com.example.mysocialandroidapp2.enumeration.EventType

@Entity
data class EventEntity(
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
    val attachment: AttachmentEmbeddable? = null,
    val link: String? = null,
){
    fun toDto() = Event(id, authorId, author, authorAvatar, content, datetime, published,
        coords?.toDto(),
        if (isOnline) EventType.ONLINE else EventType.OFFLINE,
        likeOwnerIds, likedByMe, speakerIds, participantsIds, participatedByMe,
        attachment?.toDto(),
        link)

    companion object {
        fun fromDto(dto: Event) =
            EventEntity(dto.id, dto.authorId, dto.author, dto.authorAvatar, dto.content, if (dto.datetime != null) dto.datetime.toString() else null, dto.published,
                CoordinatesEmbeddable.fromDto(dto.coords), dto.type == EventType.ONLINE,
                dto.likeOwnerIds, dto.likedByMe, dto.speakerIds, dto.participantsIds, dto.participatedByMe,
                AttachmentEmbeddable.fromDto(dto.attachment),
                dto.link)
    }
}

fun List<EventEntity>.toDto(): List<Event> = map(EventEntity::toDto)
fun List<Event>.toEntity(): List<EventEntity> {
    var eventEntities = mutableListOf<EventEntity>()
    for (event in this){
        eventEntities.add(EventEntity.fromDto(event))
    }
    return eventEntities
}