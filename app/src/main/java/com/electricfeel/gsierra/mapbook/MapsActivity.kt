package com.electricfeel.gsierra.mapbook

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
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
    private var lastLocation: LocationEngineListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**Todo move the API secret to a safer place*/
        Mapbox.getInstance(this, getString(R.string.mapbox_key))
        setContentView(R.layout.activity_maps)
        mapView = findViewById<View>(R.id.mapView) as MapView
        mapView!!.onCreate(savedInstanceState)

        val locationEngineProvider = LocationEngineProvider(this)
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable()

        lastLocation = object : LocationEngineListener {
            override fun onConnected() {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(this@MapsActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this@MapsActivity,
                            Manifest.permission.READ_CONTACTS)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(this@MapsActivity,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                Constants.PERMISSION_LOCATION)

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                }
                locationEngine!!.requestLocationUpdates()
            }

            override fun onLocationChanged(location: Location) {
                if (map != null) {
                    map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location), Constants.ZOOM_DEFAULT))
                }
            }
        }
        mapView!!.getMapAsync { mapboxMap ->
            // Customize map with markers, polylines, etc.
            map = mapboxMap
            val lastLocation = locationEngine!!.lastLocation
            if (lastLocation != null) {
                map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastLocation), Constants.ZOOM_DEFAULT))
            }
        }
        locationEngine!!.addLocationEngineListener(lastLocation)
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
}