package com.epa.mhealthservice.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import com.epa.mhealthservice.database.ChallengeDao
import com.epa.mhealthservice.database.StepsDao

class NotificationRepository(appContext: Context, val stepsDao: StepsDao, val challengeDao: ChallengeDao): BroadcastReceiver() {

    val notificationManager = appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager






    fun sendChallengeNotification(){


    }


    fun sendSummaryNotification(){

    }





    override fun onReceive(p0: Context?, p1: Intent?) {
        TODO("Not yet implemented")
    }


}