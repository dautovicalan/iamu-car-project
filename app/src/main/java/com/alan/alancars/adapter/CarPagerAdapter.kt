package com.alan.alancars.adapter

import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.alan.alancars.CAR_PROVIDER_CONTENT_URI
import com.alan.alancars.PictureOpenableActivity
import com.alan.alancars.R
import com.alan.alancars.model.Car
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

private const val IMAGE_TYPE = "image/*"

class CarPagerAdapter(private val context: Context, private val items: MutableList<Car>,
                      private val pictureOpenableActivity: PictureOpenableActivity)
    : RecyclerView.Adapter<CarPagerAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivCar = itemView.findViewById<ImageView>(R.id.ivCar)
        private val etCarMaker = itemView.findViewById<EditText>(R.id.etCarMaker)
        private val etCarModel = itemView.findViewById<EditText>(R.id.etCarModel)
        private val etCarClass = itemView.findViewById<EditText>(R.id.etCarClass)
        private val etCarCityMpg = itemView.findViewById<EditText>(R.id.etCarCityMpg)
        private val etCarYear = itemView.findViewById<EditText>(R.id.etCarYear)
        private val etCarPrice = itemView.findViewById<EditText>(R.id.etCarPrice)

        val btnUpdateCar = itemView.findViewById<AppCompatButton>(R.id.btnUpdateCar)

        fun bind(car: Car) {
            Picasso.get()
                .load(File(car.picturePath))
                .error(R.drawable.mercedes)
                .transform(RoundedCornersTransformation(50, 5))
                .into(ivCar)
            etCarMaker.setText(car.make)
            etCarModel.setText(car.model)
            etCarClass.setText(car.carClass)
            etCarCityMpg.setText(car.cityMpg.toString())
            etCarYear.setText(car.year.toString())
            etCarPrice.setText(car.price.toString())

        }

        fun prepareBindingArray() = arrayOf(
            etCarMaker,
            etCarModel,
            etCarClass,
            etCarCityMpg,
            etCarYear,
            etCarPrice
        )

        fun updateExistingCar(car: Car): Car {
            car.make = etCarMaker.text.toString().trim()
            car.model = etCarModel.text.toString().trim()
            car.carClass = etCarClass.text.toString().trim()
            car.cityMpg = etCarCityMpg.text.toString().trim().toInt()
            car.year = etCarYear.text.toString().trim().toInt()
            car.price = etCarPrice.text.toString().trim().toDouble()

            return car
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.car_pager, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var car = items[position]

        holder.btnUpdateCar.setOnClickListener {
            if (formValid(holder.prepareBindingArray())) {
                car = holder.updateExistingCar(car)

                context.contentResolver.update(
                    ContentUris.withAppendedId(CAR_PROVIDER_CONTENT_URI, car._id!!),
                    ContentValues().apply {
                        put(Car::make.name, car.make)
                        put(Car::model.name, car.model)
                        put(Car::carClass.name, car.carClass)
                        put(Car::cityMpg.name, car.cityMpg)
                        put(Car::cityMpg.name, car.cityMpg)
                        put(Car::year.name, car.year)
                        put(Car::price.name, car.price)
                    },
                    null, null
                )
                notifyItemChanged(position)
            }
        }

        holder.ivCar.setOnLongClickListener {
            car.picturePath = pictureOpenableActivity.uploadPicture()
            Picasso.get()
                .load(File(car.picturePath))
                .error(R.drawable.mercedes)
                .transform(RoundedCornersTransformation(50, 5))
                .into(holder.ivCar)
            true
        }


        holder.bind(car)
    }


    private fun formValid(arrayBinding: Array<EditText>): Boolean {
        var ok = true
        arrayBinding.forEach {
            if (it.text.toString().trim().isBlank()){
                ok = false
                it.error = "Please insert"
            }
        }
        return ok
    }


    override fun getItemCount() = items.size
}