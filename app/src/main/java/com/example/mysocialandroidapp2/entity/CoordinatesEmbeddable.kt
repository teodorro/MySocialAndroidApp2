package com.example.mysocialandroidapp2.entity

import com.example.mysocialandroidapp2.dto.Coordinates


//data class CoordinatesEmbeddable(
//    val lat: Double = 0.0,
//    val long: Double = 0.0,
//) {
//
//    fun toDto(): Coordinates = Coordinates(lat, long)
//
//    companion object {
//        fun fromDto(coordinates: Coordinates?) =
//            coordinates?.let {
//                CoordinatesEmbeddable(it.lat, it.long)
//            }
//    }
//}

data class CoordinatesEmbeddable(
    var lat: Double? = null,
    var long: Double? = null,
) {

    fun toDto(): Coordinates = Coordinates(lat = lat ?: 0.0, long = long ?: 0.0)

    companion object {
        fun fromDto(coordinates: Coordinates?): CoordinatesEmbeddable? =
            with(coordinates) {
                CoordinatesEmbeddable(lat = this?.lat, long = this?.long)
            }
    }
}