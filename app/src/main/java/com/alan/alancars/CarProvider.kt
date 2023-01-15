package com.alan.alancars

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.alan.alancars.dao.CarRepository
import com.alan.alancars.factory.getCarRepository
import com.alan.alancars.model.Car

private const val AUTHORITY = "com.alan.alancars.api.provider"
private const val PATH = "cars"
val CAR_PROVIDER_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH")

private const val ITEMS = 10
private const val ITEM_ID = 20
private val URI_MATCHER = with(UriMatcher(UriMatcher.NO_MATCH)) {
    addURI(AUTHORITY, PATH, ITEMS)
    addURI(AUTHORITY, "$PATH/#", ITEM_ID)
    this
}

class CarProvider : ContentProvider() {

    private lateinit var carRepository: CarRepository


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when(URI_MATCHER.match(uri)) {
            ITEMS -> return carRepository.delete(selection, selectionArgs)
            ITEM_ID ->
                uri.lastPathSegment?.let {
                    return carRepository.delete("${Car::_id.name}=?", arrayOf(it))
                }
        }
        throw java.lang.IllegalArgumentException("No such uri")
    }

    override fun getType(uri: Uri): String? {
        TODO(
            "Implement this to handle requests for the MIME type of the data" +
                    "at the given URI"
        )
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id = carRepository.insert(values)
        return ContentUris.withAppendedId(CAR_PROVIDER_CONTENT_URI, id)
    }

    override fun onCreate(): Boolean {
        carRepository = getCarRepository(context)
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? = carRepository.query(projection, selection, selectionArgs, sortOrder)

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        when(URI_MATCHER.match(uri)) {
            ITEMS -> return carRepository.update(values, selection, selectionArgs)
            ITEM_ID ->
                uri.lastPathSegment?.let {
                    return carRepository.update(values, "${Car::_id.name}=?", arrayOf(it))
                }
        }
        throw java.lang.IllegalArgumentException("No such uri")
    }
}