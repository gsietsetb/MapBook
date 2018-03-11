/**
 * Filename: FoursquareGroup.java
 * Author: Matthew Huie
 *
 * FoursquareGroup describes a group object from the Foursquare API.
 */

package com.electricfeel.gsierra.mapbook.model.data.foursquare

import java.util.*

class FoursquareGroup {

    // A results list within the group.
    internal var results: List<FoursquareResults> = ArrayList()
}
