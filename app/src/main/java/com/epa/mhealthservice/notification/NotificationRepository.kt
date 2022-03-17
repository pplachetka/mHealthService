package com.epa.mhealthservice.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.IntentFilter
import com.epa.mhealthservice.R
import com.epa.mhealthservice.database.Challenge
import com.epa.mhealthservice.database.ChallengeDao
import com.epa.mhealthservice.database.StepsDao
import com.epa.mhealthservice.misc.DateFetcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NotificationRepository(val appContext: Context, val challengeDao: ChallengeDao, val stepsDao: StepsDao) {

    private val notificationManager = appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager


    fun sendChallengeNotification(){

        val denyIntent = Intent(appContext, ChallengeDeniedReceiver::class.java)
        val denyPendingIntent = PendingIntent.getBroadcast(appContext, 112115, denyIntent, 0)

        val notification = Notification.Builder(appContext, "dh-service-background")
            .setContentTitle("Eine neue Herausforderung für dich!")
            .setContentText(TextFragments.createChallengeText())
            .setSmallIcon(R.drawable.flash_icon)
            .setStyle(Notification.BigTextStyle())
            .addAction(Notification.Action.Builder(null, "Kein Interesse", denyPendingIntent).build())
            .setDeleteIntent(denyPendingIntent)
            .build()

        notificationManager.notify(56226, notification)
    }


    fun sendSuccessNotification(){


        val notification = Notification.Builder(appContext, "dh-service-background")
            .setContentTitle("Eine neue Herausforderung für dich!")
            .setContentText("Stark! 💪 \nDu hast die Herausforderung abgeschlossen. Mach weiter so!")
            .setSmallIcon(R.drawable.succes_icon)
            .setStyle(Notification.BigTextStyle())
            .build()

        notificationManager.notify(56887, notification)
    }


    init {
        val receiver = SummaryReceiver()

        val filter = IntentFilter("com.epa.SUMMARY")

        appContext.registerReceiver(receiver, filter)

    }



}