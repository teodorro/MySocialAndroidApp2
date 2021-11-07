package com.example.mysocialandroidapp2.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mysocialandroidapp2.dto.User

@Entity
data class UserEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val login: String,
    val name: String,
    val avatar: Any?,
    val authorities: List<String>,
){
    fun toDto() = User(id, login, name, avatar, authorities)

    companion object {
        fun fromDto(dto: User) =
            UserEntity(dto.id, dto.login, dto.name, dto.avatar, dto.authorities)
    }
}

fun List<UserEntity>.toDto(): List<User> = map(UserEntity::toDto)