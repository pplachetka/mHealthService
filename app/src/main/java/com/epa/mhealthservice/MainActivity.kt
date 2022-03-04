package com.epa.mhealthservice

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

class MainActivity : ComponentActivity() {
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

@Composable
fun Greeting(repository: LocationRepository) {
    val text = remember{ mutableStateOf("")}

    Column {

        Button(onClick = {
            var addedText = repository.getCurrentLocation().latitude.toString()

            text.value = addedText


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