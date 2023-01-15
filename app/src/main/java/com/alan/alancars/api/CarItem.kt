package com.alan.alancars.api

import com.google.gson.annotations.SerializedName

data class CarItem(
    @SerializedName("city_mpg") val city_mpg : Int,
    @SerializedName("class") val carClass : String,
    @SerializedName("combination_mpg") val combination_mpg : Int,
    @SerializedName("cylinders") val cylinders : Int,
    @SerializedName("displacement") val displacement : Double,
    @SerializedName("drive") val drive : String,
    @SerializedName("fuel_type") val fuel_type : String,
    @SerializedName("highway_mpg") val highway_mpg : Int,
    @SerializedName("make") val make : String,
    @SerializedName("model") val model : String,
    @SerializedName("transmission") val transmission : String,
    @SerializedName("year") val year : Int,
)
