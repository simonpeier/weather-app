package ch.simonpeier.weatherapp

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.simonpeier.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: WeatherViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherLayout(Modifier, viewModel, application)
                }
            }
        }

        // Check location permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.onLocationPermissionGranted(application)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.onLocationPermissionGranted(application)
            } else {
                viewModel.onLocationPermissionDenied()
            }
        }
    }
}

@Composable
fun WeatherLayout(
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel = viewModel(),
    application: Application = Application()
) {
    val weatherUiState by weatherViewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(48.dp)
    ) {
        Text(text = weatherUiState.location, fontSize = 48.sp)
        Text(text = weatherUiState.date, fontSize = 32.sp, modifier = Modifier.padding(bottom = 40.dp))
        Text(text = weatherUiState.temperature, fontSize = 48.sp, modifier = Modifier.padding(bottom = 40.dp))
        WeatherInfoCard(
            title = "Air",
            firstDesc = "Pressure",
            firstContent = weatherUiState.airPressure,
            secondDesc = "Humidity",
            secondContent = weatherUiState.humidity
        )
        WeatherInfoCard(
            title = "Wind",
            firstDesc = "Speed",
            firstContent = weatherUiState.windSpeed,
            secondDesc = "Direction",
            secondContent = weatherUiState.windDirection
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { weatherViewModel.onLocationPermissionGranted(application) },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Fetch weather", fontSize = 24.sp)
        }
    }

    if (weatherViewModel.locationPermissionDenied) {
        LocationPermissionDeniedSnackbar()
    }
}

@Composable
fun WeatherInfoCard(
    title: String,
    firstDesc: String,
    firstContent: String,
    secondDesc: String,
    secondContent: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(bottom = 32.dp)) {
        Text(text = title, fontSize = 32.sp)
        Row(modifier = modifier) {
            Text(text = firstDesc, fontSize = 24.sp, modifier = Modifier.padding(end = 16.dp))
            Text(text = firstContent, fontSize = 24.sp, modifier = Modifier)
        }
        Row(modifier = modifier) {
            Text(text = secondDesc, fontSize = 24.sp, modifier = Modifier.padding(end = 16.dp))
            Text(text = secondContent, fontSize = 24.sp, modifier = Modifier)

        }
    }
}

@Composable
fun LocationPermissionDeniedSnackbar() {
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(50.dp)
    ) {
        Snackbar(modifier = Modifier.padding(16.dp)) {
            Text("Location permission is required for the app to function properly.")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherLayoutPreview() {
    WeatherAppTheme {
        WeatherLayout()
    }
}