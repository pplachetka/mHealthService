package com.epa.mhealthservice

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.room.Room
import com.epa.mhealthservice.database.Challenge
import com.epa.mhealthservice.database.ChallengeDao
import com.epa.mhealthservice.database.ServiceDatabase
import com.epa.mhealthservice.database.StepsDao
import com.epa.mhealthservice.location.LocationRepository
import com.epa.mhealthservice.misc.DateFetcher
import com.epa.mhealthservice.motion.MotionRepository
import com.epa.mhealthservice.notification.ChallengeDeniedReceiver
import com.epa.mhealthservice.notification.NotificationRepository
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


        val db = ServiceDatabase.buildDatabase(applicationContext)


        val repo = LocationRepository(applicationContext, db.hotspotsDao())

        val noma = NotificationRepository(applicationContext, db.challengeDao(), db.stepsDao())
/*
        val motion = MotionRepository(applicationContext, db.stepsDao())
        CoroutineScope(Dispatchers.IO + Job()).launch{

            motion.stepFlow.collect {

                withContext(Dispatchers.Main){
                    println(it.toString())
                }
            }

        }
*/




        setContent {
            MHealthServiceTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting(repo, noma, this, applicationContext, db.challengeDao(), db.stepsDao())
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalPermissionsApi
@Composable
fun Greeting(
    repository: LocationRepository,
    notificationRepository: NotificationRepository,
    context: Context,
    appContext: Context,
    challengeDao: ChallengeDao,
    stepsDao: StepsDao
) {
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

            context.sendBroadcast(Intent("com.epa.SUMMARY"))

          //  context.sendBroadcast(Intent(context, ChallengeDeniedReceiver::class.java))

          //  notificationRepository.sendChallengeNotification()

        }) {
            Text(text = "Multibutton")
        }

        Button(onClick = {


            println(context.getSharedPreferences("service-kv", Context.MODE_PRIVATE).getBoolean("challengeActive", false))

        }) {
            Text(text = "Multibutton 2")
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

