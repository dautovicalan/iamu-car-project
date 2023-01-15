package com.alan.alancars.factory

import android.content.Context
import com.alan.alancars.dao.CarSqlHelper

fun getCarRepository(context: Context?) = CarSqlHelper(context)