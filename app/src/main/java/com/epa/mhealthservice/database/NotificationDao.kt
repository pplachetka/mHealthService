package com.epa.mhealthservice.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNotification(notification: Notification)

    @Query("SELECT * FROM notification WHERE text = :text")
    fun getSimilarTexts(text:String):List<Notification>

}