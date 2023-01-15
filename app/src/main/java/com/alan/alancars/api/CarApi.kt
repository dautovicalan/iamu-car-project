package com.alan.alancars.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


const val API_URL = "https://api.api-ninjas.com/v1/"

interface CarApi {
    @GET("cars?limit=30&make=bmw")
    fun fetchItems(@Query("count") count: Int) : Call<List<CarItem>>
}