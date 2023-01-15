package com.alan.alancars.framework

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import com.alan.alancars.CAR_PROVIDER_CONTENT_URI
import com.alan.alancars.model.Car

fun View.applyAnimation(animationId: Int)
        = startAnimation(AnimationUtils.loadAnimation(context, animationId))


inline fun<reified T: Activity> Context.startActivity()
        = startActivity(
    Intent(this, T::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

inline fun<reified T: Activity> Context.startActivity(key: String, value: Int)
        = startActivity(
    Intent(this, T::class.java).apply {
        putExtra(key, value)
    }.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

inline fun<reified T: BroadcastReceiver> Context.sendBroadcast()
        = sendBroadcast(Intent(this, T::class.java))

fun Context.setBooleanPreference(key: String, value: Boolean = true) {
    PreferenceManager.getDefaultSharedPreferences(this)
        .edit()
        .putBoolean(key, value)
        .apply()
}

fun Context.getBooleanPreference(key: String) =
    PreferenceManager.getDefaultSharedPreferences(this)
        .getBoolean(key, false)

fun Context.isOnline() : Boolean{
    val connectivityManager = getSystemService<ConnectivityManager>()
    connectivityManager?.activeNetwork?.let { network ->
        connectivityManager.getNetworkCapabilities(network)?.let { cap ->
            return cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        }
    }
    return false
}


fun callDelayed(delay: Long, runnable: Runnable) {
    Handler(Looper.getMainLooper()).postDelayed(
        runnable,
        delay
    )
}

@SuppressLint("Range")
fun Context.fetchItems() : MutableList<Car> {
    val items = mutableListOf<Car>()
    val cursor = contentResolver.query(
        CAR_PROVIDER_CONTENT_URI,
        null, null, null, null)
    while (cursor != null && cursor.moveToNext()) {
        items.add(Car(
            cursor.getLong(cursor.getColumnIndex(Car::_id.name)),
            cursor.getInt(cursor.getColumnIndex(Car::cityMpg.name)),
            cursor.getString(cursor.getColumnIndex(Car::carClass.name)),
            cursor.getInt(cursor.getColumnIndex(Car::combinationMpg.name)),
            cursor.getInt(cursor.getColumnIndex(Car::cylinders.name)),
            cursor.getDouble(cursor.getColumnIndex(Car::displacement.name)),
            cursor.getString(cursor.getColumnIndex(Car::drive.name)),
            cursor.getString(cursor.getColumnIndex(Car::fuelType.name)),
            cursor.getInt(cursor.getColumnIndex(Car::highwayMpg.name)),
            cursor.getString(cursor.getColumnIndex(Car::make.name)),
            cursor.getString(cursor.getColumnIndex(Car::model.name)),
            cursor.getString(cursor.getColumnIndex(Car::transmission.name)),
            cursor.getInt(cursor.getColumnIndex(Car::year.name)),
            cursor.getString(cursor.getColumnIndex(Car::picturePath.name)),
            cursor.getDouble(cursor.getColumnIndex(Car::price.name))
        ))
    }

    return items
}