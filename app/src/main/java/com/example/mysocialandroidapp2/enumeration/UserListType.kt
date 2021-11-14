package com.example.mysocialandroidapp2.enumeration



enum class UserListType(val value: Long) {
    LIKES(1),
    MENTIONS(2),
    PARTICIPANTS(3),
    SPEAKERS(4);

    companion object {
        fun fromLong(value: Long) = UserListType.values().first { it.value == value }
    }

}