package com.epa.mhealthservice.database

import androidx.room.RoomDatabase

abstract class ServiceDatabase: RoomDatabase() {

abstract fun stepsDao():StepsDao

abstract fun notificationDao():NotificationDao

abstract fun challengeDao():ChallengeDao

abstract fun hotspotsDao():HotspotsDao

}