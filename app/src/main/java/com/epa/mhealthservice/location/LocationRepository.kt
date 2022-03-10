package com.epa.mhealthservice.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.lazy.rememberLazyListState
import com.epa.mhealthservice.database.Hotspots
import com.epa.mhealthservice.database.HotspotsDao
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

class LocationRepository(val context: Context, val hotspotDao: HotspotsDao) {

    private val _locationFlow = MutableSharedFlow<Location>(replay = 0)
    val locationFlow: SharedFlow<Location> = _locationFlow

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var updateRequest: LocationRequest

    private val scope = CoroutineScope(Dispatchers.IO + Job())


    /*
    Returns the current location once (experimental)
     */
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context) {

        val cts = CancellationTokenSource()

        fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cts.token
        ).addOnSuccessListener { result ->

            if(result != null){

                Toast.makeText(context, result.latitude.toString()  + " " + result.longitude.toString(), Toast.LENGTH_LONG).show()

                val hotspot = Hotspots(longitude = result.longitude, latitude = result.latitude)

                scope.launch {
                    hotspotDao.insertHotspot(hotspot)
                }

            }
            else{
                Toast.makeText(context, "Computing current location data failed", Toast.LENGTH_LONG).show()
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
    @SuppressLint("MissingPermission")
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


    fun deleteHotspots(){
        scope.launch {
            hotspotDao.deleteAllHotspots()
        }

    }

    fun getHotspots(){
        scope.launch {

            val hs = hotspotDao.getAllHotspots()

            withContext(Dispatchers.Main){
                Toast.makeText(context, hs.size.toString(), Toast.LENGTH_LONG).show()
            }

        }

    }


}