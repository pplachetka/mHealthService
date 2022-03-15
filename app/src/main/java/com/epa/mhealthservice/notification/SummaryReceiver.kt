package com.epa.mhealthservice.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.epa.mhealthservice.misc.SummaryService

class SummaryReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        var intent = Intent(context, SummaryService::class.java)

        context!!.startService(intent)
    }
}