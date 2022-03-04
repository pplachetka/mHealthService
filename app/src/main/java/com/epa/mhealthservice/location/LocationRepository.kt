package com.epa.mhealthservice.location

import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.MutableState
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class LocationRepository(context: Context) {

    private val _locationFlow = MutableSharedFlow<Location>(replay = 0)
    val locationFlow: SharedFlow<Location> = _locationFlow

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var updateRequest: LocationRequest

    private val scope = CoroutineScope(Dispatchers.IO + Job())




    /*
    Returns the current location once (experimental)
     */
    fun getCurrentLocation(state : MutableState<String>){

        val cts = CancellationTokenSource()
        lateinit var tempLocation: Location

        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cts.token).addOnSuccessListener { result ->
            if (result != null){

                state.value = result.latitude.toString()

            }
        }
    }


    /*
    Generates an UpdateRequest for location updates with a set frequency
     */
    fun generateUpdateRequest(frequencyInMillis:Long) {
        updateRequest = LocationRequest().apply {
            interval = frequencyInMillis
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }


    /*
    Start collecting location updates and publish them to flow
     */
    fun subscribeLocationUpdates(frequencyInMillis: Long){

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)

                scope.launch {
                    _locationFlow.emit(result.lastLocation)
                }
            }
        }

        generateUpdateRequest(frequencyInMillis)

        fusedLocationProviderClient.requestLocationUpdates(
            updateRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }


    /*
    Stop collecting location updates
     */
    fun unsubscribeLocationUpdates(){

        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    init {
        /*
        Initialize FLP
         */
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)




    }

}