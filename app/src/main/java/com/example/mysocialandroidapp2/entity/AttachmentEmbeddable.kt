package com.example.mysocialandroidapp2.entity

import com.example.mysocialandroidapp2.dto.Attachment
import com.example.mysocialandroidapp2.enum.AttachmentType

data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}