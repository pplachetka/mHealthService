package com.epa.mhealthservice

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.epa.mhealthservice.database.ServiceDatabase
import com.epa.mhealthservice.location.LocationRepository
import com.epa.mhealthservice.motion.MotionRepository
import com.epa.mhealthservice.notification.TextFragments
import com.epa.mhealthservice.ui.theme.MHealthServiceTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {
    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val db = Room.databaseBuilder(applicationContext,ServiceDatabase::class.java, "service-database").build()


        val repo = LocationRepository(applicationContext, db.hotspotsDao())
/*
        val motion = MotionRepository(applicationContext)
        CoroutineScope(Dispatchers.IO + Job()).launch{

            repo.subscribeLocationUpdates(8000)

            repo.locationFlow.collect{

                withContext(Dispatchers.Main){

                    Toast.makeText(this@MainActivity, it.longitude.toString(), Toast.LENGTH_SHORT).show()
                }

            }





/*
            motion.stepFlow.collect {

                println(it.toString())


            }

 */
        }

*/



        setContent {
            MHealthServiceTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting(repo, this, applicationContext)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalPermissionsApi
@Composable
fun Greeting(repository: LocationRepository, context: Context, appContext: Context) {
    val permissionList = listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    val permissions = rememberMultiplePermissionsState(permissions = permissionList)
    val permissions2 = rememberPermissionState(permission = Manifest.permission.ACTIVITY_RECOGNITION)

Column {

    Column(Modifier.background(Color.Gray)) {
        Row {
            Button(onClick = {

                if (!permissions.allPermissionsGranted) {
                    permissions.launchMultiplePermissionRequest()
                }

            }) {
                Text(text = "Request location permissions")
            }
            Text(text = if (permissions.allPermissionsGranted) "permissions granted" else "no permission yet")
        }

        Row {
            Button(onClick = {

                permissions2.launchPermissionRequest()

            }) {
                Text(text = "Request activity permissions")
            }
            Text(text = if (permissions2.status.isGranted) "permission granted" else "no permission yet")
        }





        Button(onClick = {

            repository.getCurrentLocation(context)

        }) {
            Text(text = "Get current location")
        }

        Button(onClick = {

            repository.getHotspots()
        }) {
            Text(text = "Get hotspot count")
        }


        Button(onClick = {
            repository.deleteHotspots()
        }) {
            Text(text = "Delete all Hotspots")
        }

        Button(onClick = {

            println(TextFragments.createSummaryText(2))

        }) {
            Text(text = "Multibutton")
        }
    }

    Column(
        Modifier
            .background(Color.Green)
            .fillMaxWidth()) {

        Button(onClick = {

            val serviceIntent = Intent(appContext, MainService::class.java)

            appContext.startForegroundService(serviceIntent)

        }) {
            Text(text = "Start Service")
        }
    }
}
}

