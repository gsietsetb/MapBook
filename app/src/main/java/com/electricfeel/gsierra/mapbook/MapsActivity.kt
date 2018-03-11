package com.electricfeel.gsierra.mapbook

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.location.LocationEngineListener
import com.mapbox.services.android.telemetry.location.LocationEngineProvider

class MapsActivity : AppCompatActivity() {
    private var mapView: MapView? = null
    private var map: MapboxMap? = null
    private var locationEngine: LocationEngine? = null
    private var lastLocationListener: LocationEngineListener? = null
    private var lastLocation: Location? = null
    private var isLocationPermissionGranted: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_key))
        setContentView(R.layout.activity_maps)

        /*Permission request before loading the map*/
        checkLocationPermission()

        /*Default constructor. In case no permission is granted:
            a) we require NetworkProvider
            b) else, we set Barcelona as the default location*/
        val defaultLocation = Location(LocationManager.NETWORK_PROVIDER)
        defaultLocation.latitude = Constants.BARCELONA_LATITUDE
        defaultLocation.longitude = Constants.BARCELONA_LONGITUDE
        lastLocation = defaultLocation

        mapView = findViewById<View>(R.id.mapView) as MapView
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync { mapboxMap ->
            map = mapboxMap
            // Map customization
            map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastLocation), Constants.ZOOM_DEFAULT))
        }
    }

    public override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    public override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    public override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    public override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            mapView!!.onSaveInstanceState(outState)
        }
    }

    fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, { _, _ ->
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(this@MapsActivity,
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    Constants.PERMISSION_LOCATION)
                        })
                        .create()
                        .show()


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        Constants.PERMISSION_LOCATION)
            }
            return false
        } else {
            return true
        }
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
                        isLocationPermissionGranted = true
                        //Request location updates:
                        val locationEngineProvider = LocationEngineProvider(this)
                        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable()

                        lastLocationListener = object : LocationEngineListener {
                            override fun onConnected() {
                                if (checkLocationPermission()) {
                                    locationEngine!!.requestLocationUpdates()
                                    lastLocation = locationEngine!!.lastLocation
                                }
                            }

                            override fun onLocationChanged(location: Location) {
                                if (map != null) {
                                    map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location), Constants.ZOOM_DEFAULT))
                                }
                            }
                        }
                    }
                    locationEngine!!.addLocationEngineListener(lastLocationListener)
                }
                return
            }
        }
    }

}