package com.electricfeel.gsierra.mapbook

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.electricfeel.gsierra.mapbook.utils.Constants
import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.location.LocationEngineListener
import com.mapbox.services.android.telemetry.location.LocationEngineProvider

/**
 * Splash screen to perform inits as Application().
 * Its onCreate method is called before any Activities or Services have been created.*/
class SplashActivity : Activity() {

    /**Location Listener*/
    private var locationEngine: LocationEngine? = null
    private var lastLocationListener: LocationEngineListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**Location Permission grant*/
        val locationEngineProvider = LocationEngineProvider(this)
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable()
        lastLocationListener = object : LocationEngineListener {
            override fun onConnected() {
                val lastLocation = if (ContextCompat.checkSelfPermission(this@SplashActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@SplashActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), Constants.PERMISSION_LOCATION)
                    locationEngine!!.requestLocationUpdates()
                    locationEngine!!.lastLocation
                } else
                /*In case no location permission is granted,
                 we use the one from Network provider (less accurate)*/
                    Location(LocationManager.NETWORK_PROVIDER)

                /*Update AppSharedPreferences*/
                prefs.lastLocationLatitude = lastLocation.latitude.toFloat()
                prefs.lastLocationLongitude = lastLocation.longitude.toFloat()

            }

            override fun onLocationChanged(location: Location) {
                prefs.lastLocationLatitude = location.latitude.toFloat()
                prefs.lastLocationLongitude = location.longitude.toFloat()
            }
        }

        /**After storing the data in AppSharedPrefs, is ready to start Maps Activity*/
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Constants.PERMISSION_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        prefs.isLocationGranted = true
                    }
                    locationEngine!!.addLocationEngineListener(lastLocationListener)
                }
                return
            }
        }
    }
}