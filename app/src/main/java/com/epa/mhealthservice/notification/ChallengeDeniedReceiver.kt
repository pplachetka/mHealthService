package com.epa.mhealthservice.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import com.epa.mhealthservice.database.Challenge
import com.epa.mhealthservice.database.ServiceDatabase
import com.epa.mhealthservice.misc.DateFetcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChallengeDeniedReceiver: BroadcastReceiver() {

    private val scope = CoroutineScope(Dispatchers.IO + Job())



    override fun onReceive(context: Context?, intent: Intent?) {

        val sharedPreferences = context!!.getSharedPreferences("service-kv", Context.MODE_PRIVATE)

        scope.launch {
            ServiceDatabase.buildDatabase(context!!).challengeDao().insertFinishedChallenge(Challenge(0, DateFetcher.getParsedToday(), false))

            sharedPreferences.edit().putBoolean("challengeActive", false)
        }

        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager


        /*
        Cancel the notification after pressing the action button
         */
        notificationManager.cancel(56226)
    }


}