package com.alan.alancars

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alan.alancars.adapter.CarPagerAdapter
import com.alan.alancars.databinding.ActivityCarPagerBinding
import com.alan.alancars.framework.fetchItems
import com.alan.alancars.model.Car

const val POSITION = "hr.algebra.nasa.position"
class CarPagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarPagerBinding
    private lateinit var items: MutableList<Car>
    private var itemPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.viewPager.adapter = CarPagerAdapter(this, items)
        binding.viewPager.currentItem = itemPosition
    }
}