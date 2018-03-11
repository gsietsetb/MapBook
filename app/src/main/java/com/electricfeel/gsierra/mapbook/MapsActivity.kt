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
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.electricfeel.gsierra.mapbook.model.api.FoursquareAPI
import com.electricfeel.gsierra.mapbook.model.data.foursquare.FoursquareVenue
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.location.LocationEngineListener
import com.mapbox.services.android.telemetry.location.LocationEngineProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*


class MapsActivity : AppCompatActivity() {
    private var mapView: MapView? = null
    private var map: MapboxMap? = null
    private var locationEngine: LocationEngine? = null
    private var lastLocationListener: LocationEngineListener? = null
    private var lastLocation: Location? = null
    private var foodVenues: List<FoursquareVenue>? = null
    private var isLocationPermissionGranted: Boolean = false

    private val foursquareAPI by lazy {
        FoursquareAPI.create()
    }

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //Remove notification bar
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_maps)

        /**Map Init*/
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
        /*Lambda for onMapReady*/
        mapView!!.getMapAsync { mapboxMap ->
            /*todo decide if Needed? */
            map = mapboxMap
            // Map customization

            mapboxMap.addMarker(MarkerOptions()
                    .position(LatLng(41.3163, 2.17603))
                    .icon(IconFactory.getInstance(this).fromResource(R.drawable.ganomad_m))
                    .title(getString(R.string.common_open_on_phone))
                    .snippet(getString(R.string.foursquare_secret)))
            mapboxMap.addMarker(MarkerViewOptions()
                    .anchor(0.5f, 0.5f)
                    .position(LatLng(41.3563, 2.13603))
                    .icon(IconFactory.getInstance(this).fromResource(R.drawable.ganomad_m))
                    .title(getString(R.string.common_open_on_phone))
                    .snippet(getString(R.string.foursquare_secret)))
            mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastLocation), Constants.ZOOM_DEFAULT))

            /**Foursquare API init*/
            searchFoodNearby()
        }
    }

    private fun searchFoodNearby() {
        val userLL = "${lastLocation!!.latitude},${lastLocation!!.latitude}"

        Log.d("GET FOURSQUARE", "GET to this: " + Constants.FOURSQUARE_BASE_URL +
                " with date: " +
                SimpleDateFormat("yyyyMMdd").format(Date()) +
                " with secrets: " + getString(R.string.foursquare_id) + getString(R.string.foursquare_secret) +
                " with location: " + userLL)
        disposable = foursquareAPI.getFoodNearby(getString(R.string.foursquare_id),
                getString(R.string.foursquare_secret),
                SimpleDateFormat("yyyyMMdd").format(Date()),
                Constants.VENUE_FOOD_ID,
                userLL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            Toast.makeText(this, "Result: " +
                                    result.response!!.venues[0].name, Toast.LENGTH_LONG).show()
                        },
                        { error ->
                            run {
                                Log.d("D/Toast", " error " + error.message + error.localizedMessage)
                                Toast.makeText(this, "Data fetching error " + error.message + error.localizedMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                )
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
        disposable?.dispose()
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