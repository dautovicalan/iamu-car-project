package com.alan.alancars.api

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.alan.alancars.CAR_PROVIDER_CONTENT_URI
import com.alan.alancars.CarReceiver
import com.alan.alancars.framework.sendBroadcast
import com.alan.alancars.handler.downloadImageAndStore
import com.alan.alancars.model.Car
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CarFetcher(private val context: Context) {
    private var carApi: CarApi
    private var client = OkHttpClient.Builder().apply {
        addInterceptor(CarInterceptor())
    }.build()
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        carApi = retrofit.create(CarApi::class.java)
    }

    fun fetchItems(count: Int) {
        val request = carApi.fetchItems(count)

        request.enqueue(object: Callback<List<CarItem>> {
            override fun onResponse(
                call: Call<List<CarItem>>,
                response: Response<List<CarItem>>
            ) {
                response?.body()?.let { populateItems(it) }
            }
            override fun onFailure(call: Call<List<CarItem>>, t: Throwable) {
                Log.e(javaClass.name, t.toString(), t)
            }
        })
    }

    private fun populateItems(carItems: List<CarItem>) {
        GlobalScope.launch {
            carItems.forEach{
                val values = ContentValues().apply {
                    put(Car::cityMpg.name, it.city_mpg)
                    put(Car::carClass.name, it.carClass)
                    put(Car::combinationMpg.name, it.combination_mpg)
                    put(Car::cylinders.name, it.cylinders)
                    put(Car::displacement.name, it.displacement)
                    put(Car::drive.name, it.drive)
                    put(Car::fuelType.name, it.fuel_type)
                    put(Car::highwayMpg.name, it.highway_mpg)
                    put(Car::make.name, it.make)
                    put(Car::model.name, it.model)
                    put(Car::transmission.name, it.transmission)
                    put(Car::year.name, it.year)
                    put(Car::picturePath.name, "")
                    put(Car::price.name, 0.0)
                }
                context.contentResolver.insert(CAR_PROVIDER_CONTENT_URI, values)
            }

            context.sendBroadcast<CarReceiver>()
        }

    }

}