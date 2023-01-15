package com.alan.alancars.adapter

import android.content.ContentUris
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alan.alancars.CAR_PROVIDER_CONTENT_URI
import com.alan.alancars.CarPagerActivity
import com.alan.alancars.POSITION
import com.alan.alancars.R
import com.alan.alancars.framework.startActivity
import com.alan.alancars.model.Car
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File

class CarAdapter(
    private val context: Context,
    private val cars: MutableList<Car>
) : RecyclerView.Adapter<CarAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val carMake = itemView.findViewById<TextView>(R.id.tvCarMake)
        private val carModel = itemView.findViewById<TextView>(R.id.tvCarModel)
        private val carYear = itemView.findViewById<TextView>(R.id.tvCarYear)
        private val carFuelType = itemView.findViewById<TextView>(R.id.tvCarFuelType)
        private val carImage = itemView.findViewById<ImageView>(R.id.ivCarImage)

        fun bind(car: Car){
            carMake.text = car.make
            carModel.text = car.model
            carYear.text = car.year.toString()
            carFuelType.text = car.fuelType
            Picasso
                .get()
                .load(File(car.picturePath))
                .error(R.drawable.bmw)
                .transform(RoundedCornersTransformation(50, 5))
                .into(carImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.car, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cars[position])

        holder.itemView.setOnClickListener {
            context.startActivity<CarPagerActivity>(POSITION, position)
        }
        holder.itemView.setOnLongClickListener {
            deleteCar(position)
            true
        }
    }

    private fun deleteCar(position: Int) {
        val car = cars[position]
        context.contentResolver.delete(
            ContentUris.withAppendedId(CAR_PROVIDER_CONTENT_URI, car._id!!),
            null,
            null
        )
        File(car.picturePath).delete()
        cars.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return cars.size
    }
}