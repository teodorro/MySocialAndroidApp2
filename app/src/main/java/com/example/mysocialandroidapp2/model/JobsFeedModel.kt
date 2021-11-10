package com.example.mysocialandroidapp2.model

import com.example.mysocialandroidapp2.dto.Job

data class JobsFeedModel(
    val jobs: List<Job> = emptyList(),
    val empty: Boolean = false,
)
