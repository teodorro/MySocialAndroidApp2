package com.example.mysocialandroidapp2.model

import com.example.mysocialandroidapp2.dto.Event


data class EventFeedModel(
    val posts: List<Event> = emptyList(),
    val empty: Boolean = false,
)