package com.epa.mhealthservice.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Steps::class, Notification::class, Challenge::class, Hotspots::class], version = 1)
abstract class ServiceDatabase: RoomDatabase() {

abstract fun stepsDao():StepsDao

abstract fun notificationDao():NotificationDao

abstract fun challengeDao():ChallengeDao

abstract fun hotspotsDao():HotspotsDao


companion object{

    fun buildDatabase(context:Context) = Room.databaseBuilder(context, ServiceDatabase::class.java, "service-database").build()
}

}