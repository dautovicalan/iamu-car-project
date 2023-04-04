package com.alan.alancars

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.alan.alancars.adapter.CarPagerAdapter
import com.alan.alancars.databinding.ActivityCarPagerBinding
import com.alan.alancars.framework.fetchItems
import com.alan.alancars.model.Car
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

const val POSITION = "hr.algebra.nasa.position"
private const val IMAGE_TYPE = "image/*"
class CarPagerActivity : AppCompatActivity(), PictureOpenableActivity {

    private lateinit var binding: ActivityCarPagerBinding
    private lateinit var items: MutableList<Car>
    private var itemPosition = 0
    private lateinit var picturePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        picturePath = ""
        initPager()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun initPager() {
        items = fetchItems()
        itemPosition = intent.getIntExtra(POSITION, itemPosition)
        binding.viewPager.adapter = CarPagerAdapter(this, items, this)
        binding.viewPager.currentItem = itemPosition
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

            val dir = this.applicationContext?.getExternalFilesDir(null)
            val file = File(dir, File.separator.toString() + UUID.randomUUID().toString() + ".jpg")

            this.contentResolver?.openInputStream(it.data?.data as Uri).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val bos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                    outputStream.write(bos.toByteArray())
                    picturePath = file.absolutePath
                }
            }
        }
    }

    override fun uploadPicture(): String {
        handleImage()
        if (!picturePath.isNullOrBlank()){
            return picturePath
        }
        return ""
    }

}