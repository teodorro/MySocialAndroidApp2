package com.example.mysocialandroidapp2.dto

import com.example.mysocialandroidapp2.enum.AttachmentType

data class Attachment(
    val url: String,
    val type: AttachmentType,
)