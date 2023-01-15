package com.alan.alancars

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alan.alancars.framework.setBooleanPreference
import com.alan.alancars.framework.startActivity

class CarReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        context.setBooleanPreference(DATA_IMPORTED)
        context.startActivity<HomeMainActivity>()
    }
}