package com.epa.mhealthservice.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Challenge(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    val date:String,
    var completed:Boolean
)
