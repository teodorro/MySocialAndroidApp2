package com.example.mysocialandroidapp2.dto

data class User(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: Object,
    val authorities: List<String>,
)