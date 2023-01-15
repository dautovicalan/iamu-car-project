package com.alan.alancars.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.alan.alancars.model.Car

private const val DB_NAME = "cars.db"
private const val DB_VERSION = 1
private const val TABLE_NAME = "cars"
private val CREATE_TABLE = "create table $TABLE_NAME( " +
        "${Car::_id.name} integer primary key autoincrement, " +
        "${Car::cityMpg.name} long not null, " +
        "${Car::carClass.name} text not null, " +
        "${Car::combinationMpg.name} integer not null, " +
        "${Car::cylinders.name} integer not null, " +
        "${Car::displacement.name} real not null, " +
        "${Car::drive.name} text not null, " +
        "${Car::fuelType.name} text not null, " +
        "${Car::highwayMpg.name} integer not null, " +
        "${Car::make.name} text not null, " +
        "${Car::model.name} text not null, " +
        "${Car::transmission.name} text not null, " +
        "${Car::year.name} integer not null, " +
        "${Car::picturePath.name} text not null, " +
        "${Car::price.name} real not null " +
        ")"
private const val DROP_TABLE = "drop table $TABLE_NAME"

class CarSqlHelper(context: Context?)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION), CarRepository {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DROP_TABLE)
        onCreate(db)
    }

    override fun delete(selection: String?, selectionArgs: Array<String>?) =
        writableDatabase.delete(TABLE_NAME, selection, selectionArgs)

    override fun insert(values: ContentValues?) =
        writableDatabase.insert(TABLE_NAME, null, values)

    override fun query(projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor = readableDatabase.query(
        TABLE_NAME,
        projection,
        selection,
        selectionArgs,
        null,
        null,
        sortOrder
    )

    override fun update(values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?
    ) = writableDatabase.update(TABLE_NAME, values, selection, selectionArgs)


}