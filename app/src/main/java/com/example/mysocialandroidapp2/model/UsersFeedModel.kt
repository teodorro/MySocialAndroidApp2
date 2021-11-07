package com.example.mysocialandroidapp2.model

import com.example.mysocialandroidapp2.dto.User

data class UsersFeedModel(
    val users: List<User> = emptyList(),
    val empty: Boolean = false,
)