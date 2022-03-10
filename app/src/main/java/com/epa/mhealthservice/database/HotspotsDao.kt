package com.epa.mhealthservice.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HotspotsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHotspot(hotspot:Hotspots)

    @Query("SELECT * FROM hotspots")
    fun getAllHotspots():List<Hotspots>

    @Query("DELETE FROM hotspots")
    fun deleteAllHotspots()
}