package com.epa.mhealthservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.room.RoomDatabase

class MainService: Service() {

    val binder = ServiceBinder()
    lateinit var db: RoomDatabase






    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {



        return super.onStartCommand(intent, flags, startId)
    }


    /*
    Binder implementation with single service return function
     */
    inner class ServiceBinder: Binder(){
        fun getService():MainService = this@MainService
    }


}