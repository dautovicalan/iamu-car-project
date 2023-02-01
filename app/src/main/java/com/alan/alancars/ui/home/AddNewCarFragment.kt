package com.alan.alancars.ui.home

import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.alan.alancars.CAR_PROVIDER_CONTENT_URI
import com.alan.alancars.R
import com.alan.alancars.databinding.FragmentAddNewCarBinding
import com.alan.alancars.model.Car
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

private const val IMAGE_TYPE = "image/*"

class AddNewCarFragment : Fragment() {
    private var _binding: FragmentAddNewCarBinding? = null

    private val binding get() = _binding!!

    private lateinit var picturePath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNewCarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        picturePath = ""
        setupListeners();
    }

    private fun setupListeners() {
        binding.btnAddCar.setOnClickListener {
            if (formValid()){
                val car = Car(
                    null,
                    binding.etCarCityMpg.text.toString().trim().toInt(),
                    binding.etCarClass.text.toString().trim(),
                    binding.etCarCombinationMpg.text.toString().trim().toInt(),
                    binding.etCylinders.text.toString().trim().toInt(),
                    binding.etCarDisplacement.text.toString().trim().toDouble(),
                    binding.etCarDrive.text.toString().trim(),
                    binding.etCarFuelType.text.toString().trim(),
                    binding.etCarHighwayMpg.text.toString().trim().toInt(),
                    binding.etCarMaker.text.toString().trim(),
                    binding.etCarModel.text.toString().trim(),
                    binding.etCarTransmission.text.toString().trim(),
                    binding.etCarYear.text.toString().trim().toInt(),
                    picturePath,
                    binding.etCarPrice.text.toString().trim().toDouble()
                )

                GlobalScope.launch {
                    context?.contentResolver?.insert(
                        CAR_PROVIDER_CONTENT_URI,
                        ContentValues().apply {
                            put(Car::cityMpg.name, car.cityMpg)
                            put(Car::carClass.name, car.carClass)
                            put(Car::combinationMpg.name, car.combinationMpg)
                            put(Car::cylinders.name, car.cylinders)
                            put(Car::displacement.name, car.displacement)
                            put(Car::drive.name, car.drive)
                            put(Car::fuelType.name, car.fuelType)
                            put(Car::highwayMpg.name, car.highwayMpg)
                            put(Car::make.name, car.make)
                            put(Car::model.name, car.model)
                            put(Car::transmission.name, car.transmission)
                            put(Car::year.name, car.year)
                            put(Car::picturePath.name, car.picturePath)
                            put(Car::price.name, car.price)
                        }
                    )
                }
                findNavController().navigate(R.id.action_nav_add_new_car_to_nav_home)
            }
        }

        binding.ivCar.setOnLongClickListener {
            handleImage()
            true
        }
    }

    private fun handleImage() {
        Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = IMAGE_TYPE
            imageResult.launch(this)
        }
    }

    private val imageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK && it.data != null){

            val dir = context?.applicationContext?.getExternalFilesDir(null)
            val file = File(dir, File.separator.toString() + UUID.randomUUID().toString() + ".jpg")

            context?.contentResolver?.openInputStream(it.data?.data as Uri).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val bos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                    outputStream.write(bos.toByteArray())
                    picturePath = file.absolutePath
                    bindPicture()
                }
            }
        }
    }

    private fun bindPicture() {
        if (!picturePath.isNullOrBlank()){
            Picasso.get()
                .load(File(picturePath))
                .error(R.mipmap.ic_launcher)
                .transform(RoundedCornersTransformation(50, 5))
                .into(binding.ivCar)
        }
    }


    private fun formValid(): Boolean {
        var ok = true

        arrayOf(
            binding.etCarCityMpg,
            binding.etCarCombinationMpg,
            binding.etCylinders,
            binding.etCarDisplacement,
            binding.etCarDrive,
            binding.etCarFuelType,
            binding.etCarHighwayMpg,
            binding.etCarTransmission,
            binding.etCarMaker,
            binding.etCarModel,
            binding.etCarClass,
            binding.etCarCityMpg,
            binding.etCarPrice,
            binding.etCarYear)
            .forEach {
                if (it.text.toString().trim().isBlank()){
                    ok = false
                    it.error = "Please insert"
                }
            }

        if (picturePath.isNullOrBlank()){
            view?.let { Snackbar.make(it, "Please upload picture", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show() }
        }

        return ok && !picturePath.isNullOrBlank()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}