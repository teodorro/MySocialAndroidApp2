package com.example.mysocialandroidapp2.dto

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val coords: Coordinates? = null,
    val link: String? = null,
    val mentionIds: MutableSet<Long> = mutableSetOf(),
    val mentionedMe: Boolean = false,
    val likeOwnerIds: Set<Long> = emptySet(),
    val attachment: Attachment? = null,
)
