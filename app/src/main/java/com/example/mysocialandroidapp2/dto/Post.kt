package com.example.mysocialandroidapp2.dto

data class Post(
    val id: Long,
    val authorId: Long,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val attachment: Attachment? = null,
)
