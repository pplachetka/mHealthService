package com.epa.mhealthservice.misc

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.room.Room
import com.epa.mhealthservice.R
import com.epa.mhealthservice.database.Challenge
import com.epa.mhealthservice.database.ServiceDatabase
import com.epa.mhealthservice.database.Steps
import com.epa.mhealthservice.notification.TextFragments
import kotlinx.coroutines.*

class SummaryService: Service() {

    val binder = SummaryBinder()

    val scope = CoroutineScope(Dispatchers.IO + Job())

    lateinit var db: ServiceDatabase




    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        db = ServiceDatabase.buildDatabase(applicationContext)
        val challengeDao = db.challengeDao()
        val stepsDao = db.stepsDao()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager


        var challengeList = emptyList<Challenge>()
        var steps = 5

        val asyncList = scope.async {
            challengeDao.getDailyChallenges(DateFetcher.getParsedToday()).filter {
                it.completed
            }
        }

        val asyncSteps = scope.async {

            stepsDao.getStepsByDate(DateFetcher.getParsedToday())
        }

        scope.launch(Dispatchers.Main){

            val notification = Notification.Builder(applicationContext, "dh-service-background")
                .setContentTitle("Tägliche Zusammenfassung")
                .setContentText(TextFragments.createSummaryText(asyncList.await().size, asyncSteps.await()))
                .setSmallIcon(R.drawable.flash_icon)
                .setStyle(Notification.BigTextStyle())
                .build()

            notificationManager.notify(56892, notification)

        }

       // println(steps.toString())




        stopSelf()

        return START_NOT_STICKY
    }

















    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    inner class SummaryBinder: Binder(){
        fun getService():SummaryService = this@SummaryService
    }
}