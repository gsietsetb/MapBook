package com.electricfeel.gsierra.mapbook.utils

import android.content.Context
import android.content.SharedPreferences
import com.electricfeel.gsierra.mapbook.model.data.foursquare.FoursquareVenue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.geometry.LatLng


/**
 * Created by gsierra on 12/03/2018.
 */

class SharedPrefs(context: Context) {
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(Constants.PREFS_FILENAME, 0)

    /**Location*/
    var isLocationGranted: Boolean
        get() = sharedPrefs.getBoolean(Constants.IS_LOCATION_GRANTED, false)
        set(value) = sharedPrefs.edit().putBoolean(Constants.IS_LOCATION_GRANTED, value).apply()

    var lastLocationLatitude: Float
        get() = sharedPrefs.getFloat(Constants.LAST_LOCATION_LATITUDE, Constants.BARCELONA_LATITUDE.toFloat())
        set(value) = sharedPrefs.edit().putFloat(Constants.LAST_LOCATION_LATITUDE, value).apply()

    var lastLocationLongitude: Float
        get() = sharedPrefs.getFloat(Constants.LAST_LOCATION_LONGITUDE, Constants.BARCELONA_LONGITUDE.toFloat())
        set(value) = sharedPrefs.edit().putFloat(Constants.LAST_LOCATION_LONGITUDE, value).apply()

    val lastLocation: LatLng?
        get() = LatLng(lastLocationLatitude.toDouble(), lastLocationLongitude.toDouble())

    /**Foursquare*/
    var nearbyFoodListGson: String
        get() = sharedPrefs.getString(Constants.NEARBY_FOOD_JSON, "")
        set(value) = sharedPrefs.edit().putString(Constants.NEARBY_FOOD_JSON, value).apply()

    val nearbyFoodList: List<FoursquareVenue>
        get() = Gson().fromJson(nearbyFoodListGson, object : TypeToken<List<FoursquareVenue>>() {}.type)

    /**Booking*/
    var currentBooking: String
        get() = sharedPrefs.getString(Constants.CURRENT_BOOKING, "")
        set(value) = sharedPrefs.edit().putString(Constants.CURRENT_BOOKING, value).apply()

    var withBooking: Boolean
        get() = sharedPrefs.getBoolean(Constants.WITH_BOOKING, false)
        set(value) = sharedPrefs.edit().putBoolean(Constants.WITH_BOOKING, value).apply()

    val markerBooking: Marker
        get() = Gson().fromJson(currentBooking, object : TypeToken<Marker>() {}.type)
}