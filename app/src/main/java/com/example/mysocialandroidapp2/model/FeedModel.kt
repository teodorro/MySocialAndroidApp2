package com.example.mysocialandroidapp2.model

import com.example.mysocialandroidapp2.dto.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false,
)