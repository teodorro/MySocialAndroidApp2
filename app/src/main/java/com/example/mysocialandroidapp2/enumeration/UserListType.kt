package com.example.mysocialandroidapp2.enumeration



enum class UserListType(val value: Long) {
    LIKES(1),
    MENTIONS(2);

    companion object {
        fun fromLong(value: Long) = UserListType.values().first { it.value == value }
    }

}