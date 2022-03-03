package com.epa.mhealthservice.location

import android.location.Location
import com.google.android.gms.location.LocationCallback
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class LocationRepository {

    private val _locationFlow = MutableSharedFlow<Location>(replay = 0)
    val locationFlow: SharedFlow<Location> = _locationFlow





    /*
    Returns the current location once
     */
    fun getCurrentLocation():Location{

    }


    /*
    Generates an UpdateRequest for location updates with a set frequency
     */
    fun generateUpdateRequest(frequencyInMillis:Long): LocationCallback {

    }


    /*
    Start collecting location updates and publish them to flow
     */
    fun subscribeLocationUpdates(){

    }


    /*
    Stop collecting location updates
     */
    fun unsubscribeLocationUpdates(){

    }


}