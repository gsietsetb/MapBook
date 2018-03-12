package com.electricfeel.gsierra.mapbook

import android.app.Application
import com.electricfeel.gsierra.mapbook.utils.SharedPrefs
import com.mapbox.mapboxsdk.Mapbox

/**
 * Created by gsierra on 12/03/2018.
 */

val prefs: SharedPrefs by lazy {
    App.prefs!!
}

class App : Application() {
    companion object {
        var prefs: SharedPrefs? = null
    }

    override fun onCreate() {
        prefs = SharedPrefs(applicationContext)
        /**In further implementation, API secrets should be moved
         *  to a safer place (i.e. password.preferences file added to .gitignore)*/
        Mapbox.getInstance(this, getString(R.string.mapbox_key))
        super.onCreate()
    }
}
