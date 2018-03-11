package com.electricfeel.gsierra.mapbook.model

import com.electricfeel.gsierra.mapbook.model.data.foursquare.FoursquareJSON
import com.electricfeel.gsierra.mapbook.model.data.foursquare.FoursquareVenue

/**
 * Created by gsierra on 11/03/2018.
 */
object Model {
    data class Response(val fJson: FoursquareJSON)
    data class Group(val venueResults: List<FoursquareVenue>)
}