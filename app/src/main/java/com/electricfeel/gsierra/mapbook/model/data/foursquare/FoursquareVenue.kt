package com.electricfeel.gsierra.mapbook.model.data.foursquare

/**
 * Created by gsierra on 11/03/2018.
 */

class FoursquareVenue {

    // The ID of the venue.
    internal var id: String? = null

    // The name of the venue.
    internal var name: String? = null

    // The rating of the venue, if available.
    internal var rating: Double = 0.toDouble()

    // A location object within the venue.
    internal var location: FoursquareLocation? = null
}