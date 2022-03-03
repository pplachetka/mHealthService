package com.epa.mhealthservice.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StepsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSteps(steps:Steps)

    @Query("SELECT * FROM steps WHERE date = :date")
    fun getStepsByDate(date:String)

}