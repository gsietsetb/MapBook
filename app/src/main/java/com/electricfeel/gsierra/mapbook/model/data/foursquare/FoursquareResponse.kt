/**
 * Filename: FoursquareResponse.java
 * Author: Matthew Huie
 *
 * FoursquareResponse describes a response object from the Foursquare API.
 */

package com.electricfeel.gsierra.mapbook.model.data.foursquare

import java.util.*

class FoursquareResponse {

    // A group object within the response.
    internal var group: FoursquareGroup? = null
    internal var venues: List<FoursquareVenue> = ArrayList()

}