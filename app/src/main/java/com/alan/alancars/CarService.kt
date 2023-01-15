package com.alan.alancars

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.alan.alancars.api.CarFetcher


private const val JOB_ID = 1
@Suppress("DEPRECATION")
class CarService : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        CarFetcher(this).fetchItems(10)
    }

    companion object {
        fun enqueue(context: Context) {
            enqueueWork(context, CarService::class.java, JOB_ID,
                            Intent(context, CarService::class.java))
        }
    }
}