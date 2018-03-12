package com.electricfeel.gsierra.mapbook

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.electricfeel.gsierra.mapbook.model.api.FoursquareAPI
import com.electricfeel.gsierra.mapbook.utils.Constants
import com.google.gson.Gson
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_maps.*
import java.text.SimpleDateFormat
import java.util.*


class MapsActivity : AppCompatActivity() {
    /**Map */
    private var mapView: MapView? = null
    private var map: MapboxMap? = null

    /**Foursquare*/
    private val foursquareAPI by lazy {
        FoursquareAPI.create()
    }
    private var disposable: Disposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Remove notification bar on runtime(Fullscreen)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_maps)

        /**Foursquare data load*/
        searchFoodNearby()

        /**Map Init*/
        mapView = findViewById<View>(R.id.mapView) as MapView
        mapView!!.onCreate(savedInstanceState)
        /*Lambda for onMapReady*/
        mapView!!.getMapAsync { mapboxMap ->
            map = mapboxMap
            /*shows the map in case there is no bookings*/
            if (prefs.withBooking) showVenueDetails(mapboxMap, prefs.markerBooking)
            else addVenuesToMap(mapboxMap)
        }
    }

    private fun searchFoodNearby() {
        val userLastLocation = "${prefs.lastLocationLatitude},${prefs.lastLocationLongitude}"

        disposable = foursquareAPI.getFoodNearby(getString(R.string.foursquare_id),
                getString(R.string.foursquare_secret),
                SimpleDateFormat("yyyyMMdd").format(Date()),
                Constants.VENUE_FOOD_ID,
                userLastLocation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        /**We create an interemediate GSON object to store it in SharedPrefs*/
                        { result ->
                            run {
                                prefs.nearbyFoodListGson = Gson().toJson(result.response!!.venues)
                                addVenuesToMap(map!!)
                            }
                        },
                        { error ->
                            run {
                                Log.d("D/Toast", " error " + error.message + error.localizedMessage)
                                Toast.makeText(this, "Data fetching error " + error.message + error.localizedMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                )
    }

    private fun addVenuesToMap(mapboxMap: MapboxMap) {
        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(prefs.lastLocation!!, Constants.ZOOM_DEFAULT))
        /**Retrieve data from AppSharedPrefs as Gson -> List<FoursquareVenue>*/
        prefs.nearbyFoodList.forEach { venue ->
            mapboxMap.addMarker(MarkerOptions()
                    .position(LatLng(venue.location!!.lat, venue.location!!.lng))
                    .icon(IconFactory.getInstance(this).fromResource(R.drawable.ganomad_m))
                    .title(venue.name)
                    .snippet(venue.location!!.address))
        }
        mapboxMap.setOnMarkerClickListener { marker ->
            showVenueDetails(mapboxMap, marker)
            true
        }
    }

    private fun showVenueDetails(mapboxMap: MapboxMap, marker: Marker) {
        /*Zoom-in to the marker*/
        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, Constants.ZOOM_DEFAULT + 2))
        venue_detail_overlap.visibility = View.VISIBLE
        venue_detail_title.text = marker.title
        venue_detail_info.text = marker.snippet
        venue_detail_book.setOnClickListener { _ -> bookVenue(mapboxMap, marker) }
        venue_detail_cancel.setOnClickListener { _ -> cancelVenueBooking() }
    }

    private fun bookVenue(mapboxMap: MapboxMap, marker: Marker) {
        /*Remove all other markers and re-sets the current one*/
        mapboxMap.clear()
        mapboxMap.addMarker(MarkerOptions()
                .position(LatLng(marker.position))
                .icon(IconFactory.getInstance(this).fromResource(R.drawable.ganomad_m))
                .title(marker.title)
                .snippet(marker.snippet))

        /**Sets color tint (green)*/
        venue_detail_book.setColorFilter(Color.argb(255, 5, 255, 5))
//        venue_detail_book.setColorFilter(ContextCompat.getColor(this@MapsActivity, R.color.colorAccent), PorterDuff.Mode.MULTIPLY)

        /*Shows a toast*/
        Toast.makeText(this@MapsActivity, "Congratulations! You just booked: ${marker.snippet} Thanks a lot! ", Toast.LENGTH_LONG).show()
        venue_detail_info.text = "✅ Booked! ${marker.snippet} \n Thanks a lot! "
        /*Shows the Cancel button*/
        venue_detail_cancel.visibility = View.VISIBLE
        venue_detail_book.setOnClickListener { cancelVenueBooking() }

        venue_detail_book.animate().alpha(0.0f).duration = 2000

        /*Updates the AppSharedPrefs and stores the booking*/
        prefs.withBooking = true
        /*todo investigate: For any reason it fails to store as GSON...*/
//        prefs.currentBooking = Gson().toJson(marker)
    }

    private fun cancelVenueBooking() {
        /*Updates the AppSharedPrefs*/
        prefs.withBooking = false
        venue_detail_book.animate().alpha(1.0f).duration = 2000
        venue_detail_overlap.visibility = View.GONE
        venue_detail_cancel.visibility = View.GONE
        addVenuesToMap(map!!)
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
        disposable!!.dispose()
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