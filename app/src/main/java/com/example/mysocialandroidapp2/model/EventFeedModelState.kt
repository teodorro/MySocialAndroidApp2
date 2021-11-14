package com.example.mysocialandroidapp2.model

data class EventFeedModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
)
