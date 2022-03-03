package com.epa.mhealthservice.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Hotspots(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    val longitude:Long,
    val latitude:Long
)
