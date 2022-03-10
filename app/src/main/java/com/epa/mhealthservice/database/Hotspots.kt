package com.epa.mhealthservice.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Hotspots(
    @PrimaryKey(autoGenerate = true) var id:Int? = null,
    val longitude:Double,
    val latitude:Double
)
