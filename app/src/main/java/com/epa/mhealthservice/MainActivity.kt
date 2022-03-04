package com.epa.mhealthservice

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.epa.mhealthservice.location.LocationRepository
import com.epa.mhealthservice.ui.theme.MHealthServiceTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class MainActivity : ComponentActivity() {
    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repo = LocationRepository(applicationContext)



        setContent {
            MHealthServiceTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting(repo)
                }
            }
        }
    }
}

@ExperimentalPermissionsApi
@Composable
fun Greeting(repository: LocationRepository) {
    val text = remember{ mutableStateOf("")}
    val permissionList = listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    val permissions = rememberMultiplePermissionsState(permissions = permissionList)

    Column {
        Text(text = if(permissions.allPermissionsGranted) "permissions granted" else "no permission yet")
        Button(onClick = { 
            
            if(!permissions.allPermissionsGranted){
                permissions.launchMultiplePermissionRequest()
            }
            
        }) {
            Text(text = "Request permissions")
        }

        Button(onClick = {
            var addedText = repository.getCurrentLocation(text)
            

        }) {
            Text(text = "Get current location")
        }

        Text(text = text.value)
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MHealthServiceTheme {
    }
}