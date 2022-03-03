package com.epa.mhealthservice.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Steps(
    @PrimaryKey(autoGenerate = false) val date:String,
    val stepCount:Int
    )
