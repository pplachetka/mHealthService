package com.epa.mhealthservice.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Challenge(
    @PrimaryKey(autoGenerate = false) val date:String,
    val completed:Boolean
)
