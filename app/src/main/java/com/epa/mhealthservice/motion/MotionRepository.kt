package com.epa.mhealthservice.motion

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.epa.mhealthservice.database.Steps
import com.epa.mhealthservice.database.StepsDao
import com.epa.mhealthservice.misc.DateFetcher
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow

class MotionRepository(val context: Context, val stepsDao: StepsDao): SensorEventListener {


    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    var stepcount: Int = 0
    var stepFlow = MutableSharedFlow<Int>(replay = 0)

    private val scope = CoroutineScope(Dispatchers.IO + Job())



    init {

        sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return


        event.values.firstOrNull()?.let {

            scope.launch {

                stepcount++
                stepFlow.emit(stepcount)

                //if(stepcount % 20 == 0){
                    stepsDao.insertSteps(Steps(DateFetcher.getParsedToday(), stepcount))
                //}
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        print("onAccuracyChanged passed")
    }


    fun resetCounter(){
        stepcount = 0
    }
}