package com.epa.mhealthservice.misc

import java.util.*
import java.util.Calendar.DAY_OF_YEAR
import java.util.Calendar.YEAR


object DateFetcher {

    fun getParsedToday():String{

        val calendar = Calendar.getInstance()

        return calendar.get(DAY_OF_YEAR).toString() + "-" + calendar.get(YEAR).toString()
    }
}