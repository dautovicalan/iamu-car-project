package com.alan.alancars.model

data class Car(
    var _id: Long? = null,
    var cityMpg: Int,
    var carClass: String,
    var combinationMpg: Int,
    var cylinders: Int,
    var displacement: Double,
    var drive: String,
    var fuelType: String,
    var highwayMpg: Int,
    var make: String,
    var model: String,
    var transmission: String,
    var year: Int,
    var picturePath: String,
    var price: Double
) {
    override fun toString(): String = "$make: $model"
}
