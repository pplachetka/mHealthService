package com.epa.mhealthservice.motion

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MotionRepository(val context: Context): SensorEventListener {


    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    var stepcount: Int = 0
    var stepFlow = MutableSharedFlow<Int>(replay = 0)

    val scope = CoroutineScope(Dispatchers.IO + Job())



    init {

        sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return


        event.values.firstOrNull()?.let {

            scope.launch {

                stepcount++
                stepFlow.emit(stepcount)
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