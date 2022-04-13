package com.epa.mhealthservice

import android.app.*
import android.content.Context
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
import com.epa.mhealthservice.misc.DateFetcher
import com.epa.mhealthservice.motion.MotionRepository
import com.epa.mhealthservice.notification.NotificationRepository
import com.epa.mhealthservice.notification.SummaryReceiver
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.*

class MainService: Service() {

    val FOREGROUND_CHANNEL: String = "dh-service-foreground"
    val FOREGROUND_NOTIFICATION_ID: Int = 7121996
    val BACKGROUND_CHANNEL: String = "dh-service-background"
    val BACKGROUND_NOTIFICATION_ID: String = "background-not_id"

    val binder = ServiceBinder()

    lateinit var db: ServiceDatabase
    lateinit var hotspotlist: List<Hotspots>

    lateinit var locationRepository: LocationRepository
    lateinit var motionRepository: MotionRepository
    lateinit var notificationRepository: NotificationRepository

    val scope = CoroutineScope(Dispatchers.IO + Job())

    var previousPosition: Location = Location("empty").apply {
        latitude=51.168359
        longitude=-28.082478
    }




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
        db = ServiceDatabase.buildDatabase(applicationContext)
        locationRepository = LocationRepository(this, db.hotspotsDao())
        motionRepository = MotionRepository(applicationContext, db.stepsDao())
        notificationRepository = NotificationRepository(applicationContext, db.challengeDao(), db.stepsDao())

        /*
        Setup AlarmManager for daily summary notifications
         */
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(applicationContext, SummaryReceiver::class.java)
        val alarmPendingIntent = PendingIntent.getBroadcast(applicationContext, 42069, alarmIntent, 0)
        val time = Calendar.getInstance()
        time.let {
            it.set(Calendar.HOUR_OF_DAY, 21)
            it.set(Calendar.MINUTE, 0)
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time.timeInMillis, 24*60*60*1000, alarmPendingIntent)

        scope.launch {
            hotspotlist = db.hotspotsDao().getAllHotspots()
        }



        val foregroundNotification = Notification.Builder(this, FOREGROUND_CHANNEL)
            .setContentTitle("Digital mHealth App")
            .setContentText("Digital Health Service läuft gerade...")
            .setSmallIcon(R.drawable.foreground_notification_icon)
            .build()

        val foregroundChannel = NotificationChannel(FOREGROUND_CHANNEL, "Digital Health Foreground Channel", NotificationManager.IMPORTANCE_DEFAULT)
        val backgroundChannel = NotificationChannel(BACKGROUND_CHANNEL, "Digital Health Background Channel", NotificationManager.IMPORTANCE_HIGH)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannels(listOf(foregroundChannel, backgroundChannel))


        startForeground(FOREGROUND_NOTIFICATION_ID, foregroundNotification)


        scope.launch {
            motionRepository.stepFlow.collect {
                withContext(Dispatchers.Main){
                    println(it.toString())
                }
            }
        }

        connectToUpdates()

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

            locationRepository.subscribeLocationUpdates(5000)

            locationRepository.locationFlow.collect{ locationUpdate ->

                val sharedPreferences = applicationContext.getSharedPreferences("service-kv", Context.MODE_PRIVATE)
                val challengeActive = sharedPreferences.getBoolean("challengeActive", false)


                withContext(Dispatchers.Main){
                }

                    println("Longitude: ${locationUpdate.longitude}, Latitude: ${locationUpdate.latitude}")
                    println("In challenge: " + challengeActive.toString())
                println(previousPosition.distanceTo(locationUpdate))




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
                    when(challengeActive){

                        true -> {

                            if(locationUpdate.distanceTo(destinationLocation) < 30 && previousPosition.hasAccuracy()){

                                scope.launch {
                                    db.challengeDao().insertFinishedChallenge(Challenge(0, DateFetcher.getParsedToday(), true))
                                }

                                notificationRepository.sendSuccessNotification()

                                sharedPreferences.edit().putBoolean("challengeActive", false).commit()

                            }
                        }

                        /*
                        Check if user is completing a challenge. If not, propose a new challenge if the previous update was not in radius of a hotspot yet.
                        */
                        false -> {

                            if(locationUpdate.distanceTo(destinationLocation) < 500         //
                                && previousPosition.distanceTo(destinationLocation) > 500   //
                                && previousPosition.hasAccuracy()                           //Distinction between artificial starting point and natural update
                                && previousPosition.distanceTo(locationUpdate) >= 20)       //Distance made between two updates to measure movement speed ->
                                {                                                           //20m ~ movement of tram (15 to 20 km/h ~ 4 to 5.5 m/s) * 10 sec (update freq.)

                                notificationRepository.sendChallengeNotification()

                                sharedPreferences.edit().putBoolean("challengeActive", true).commit()

                            }
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



}