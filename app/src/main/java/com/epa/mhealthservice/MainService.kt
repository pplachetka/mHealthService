package com.epa.mhealthservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import androidx.room.Room
import androidx.room.RoomDatabase
import com.epa.mhealthservice.database.Challenge
import com.epa.mhealthservice.database.Hotspots
import com.epa.mhealthservice.database.ServiceDatabase
import com.epa.mhealthservice.location.LocationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainService: Service() {

    val FOREGROUND_CHANNEL: String = "dh-service-foreground"
    val FOREGROUND_NOTIFICATION_ID: Int = 7121996
    val BACKGROUND_CHANNEL: String = "dh-service-background"
    val BACKGROUND_NOTIFICATION_ID: String = "background-not_id"

    val binder = ServiceBinder()

    lateinit var db: ServiceDatabase
    lateinit var hotspotlist: List<Hotspots>

    lateinit var locationRepository: LocationRepository

    val scope = CoroutineScope(Dispatchers.IO + Job())

    var currentChallenge:Boolean = false
    var previousPosition: Location = Location("empty")




    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }


    /*
    Obligatory call of the service when starting the work
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        /*
        Init some values depending on applicationContext
         */
        db = Room.databaseBuilder(applicationContext, ServiceDatabase::class.java, "service-database").build()
        locationRepository = LocationRepository(this, db.hotspotsDao())

        scope.launch {
            hotspotlist = db.hotspotsDao().getAllHotspots()
        }



        val foregroundNotification = Notification.Builder(this, FOREGROUND_CHANNEL)
            .setContentTitle("Digital mHealth App")
            .setContentText("Digital Health Service is running...")
            .setSmallIcon(R.drawable.foreground_notification_icon)
            .build()

        val foregroundChannel = NotificationChannel(FOREGROUND_CHANNEL, "Digital Health Foreground Channel", NotificationManager.IMPORTANCE_DEFAULT)
        val backgroundChannel = NotificationChannel(BACKGROUND_CHANNEL, "Digital Health Background Channel", NotificationManager.IMPORTANCE_HIGH)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannels(listOf(foregroundChannel, backgroundChannel))


        startForeground(FOREGROUND_NOTIFICATION_ID, foregroundNotification)



        return START_STICKY
    }


    /*
    Binder implementation with single service return function
     */
    inner class ServiceBinder: Binder(){
        fun getService():MainService = this@MainService
    }

    /*
    The main reasoning process: when a new location update comes in from the update flow,
     choose whether to send a notification and create a challenge or not
    */
    fun connectToUpdates(){

        scope.launch {

            locationRepository.subscribeLocationUpdates(10000)

            locationRepository.locationFlow.collect{ locationUpdate ->

                for(location in hotspotlist){

                    val destinationLocation = Location("service-destination")
                    destinationLocation.apply {
                        longitude = location.longitude
                        latitude = location.latitude
                    }

                    /*
                    First check if a challenge is running and if so, check if the distance is less than GPS precision tolerance radius --> send notification
                    if challenge is successful and switch challenge status, else wait for further updates to confirm possible success
                     */
                    if(currentChallenge){
                        if(locationUpdate.distanceTo(destinationLocation) < 30){


                        }
                    }

                    /*
                    Check if user is completing a challenge. If not, propose a new challenge if the previous update was not in radius of a hotspot yet.
                     */
                    else{
                        if(!currentChallenge)
                            if(locationUpdate.distanceTo(destinationLocation) < 500 && locationUpdate.distanceTo(previousPosition) > 500){

                                currentChallenge = true
                            }
                    }



                }

                previousPosition = locationUpdate
            }

        }

    }



    /*
    Unsubscribe from location updates and stop the callback resulting in interventions
     */
    fun disconnectFromUpdates(){

        locationRepository.unsubscribeLocationUpdates()

    }

    fun createDailySummary(){

    }


}