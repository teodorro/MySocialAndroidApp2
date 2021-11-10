package com.example.mysocialandroidapp2.model

import com.example.mysocialandroidapp2.dto.Job

data class JobsFeedModel(
    val users: List<Job> = emptyList(),
    val empty: Boolean = false,
)
