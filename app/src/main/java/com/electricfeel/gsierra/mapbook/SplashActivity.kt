package com.electricfeel.gsierra.mapbook

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mapbox.mapboxsdk.Mapbox


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**todo
         *  move API secrets to a safer place
         * (i.e. password.properties added to .gitignore)*/
        Mapbox.getInstance(this, getString(R.string.mapbox_key))

        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
        finish()
    }
}