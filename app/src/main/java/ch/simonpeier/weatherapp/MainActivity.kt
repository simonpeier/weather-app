package ch.simonpeier.weatherapp

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
                    WeatherLayout(weatherViewModel = viewModel, application = application)
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
    val mediumPad = dimensionResource(R.dimen.padding_medium)
    val smallPad = dimensionResource(R.dimen.padding_small)

    Column(modifier = modifier.fillMaxSize()) {
        WeatherCard(
            paddingStart = 0.dp,
            paddingEnd = 0.dp,
            paddingTop = mediumPad,
            paddingBottom = 0.dp,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = modifier.padding(mediumPad)) {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(bottom = mediumPad),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Text(
                        text = weatherUiState.temperature,
                        style = MaterialTheme.typography.displayLarge,
                    )
                    Image(
                        painter = painterResource(weatherUiState.weatherIcon),
                        contentDescription = "weather icon",
                        contentScale = ContentScale.Crop,
                        modifier = modifier.size(76.dp)
                    )
                }
                Text(
                    text = weatherUiState.location,
                    style = MaterialTheme.typography.displayMedium,
                )
                Text(
                    text = weatherUiState.date,
                    style = MaterialTheme.typography.displaySmall,
                )
            }
        }

        WeatherCard {
            WeatherInfoColumn(
                title = "Air",
                firstDesc = "Pressure",
                firstContent = weatherUiState.airPressure,
                secondDesc = "Humidity",
                secondContent = weatherUiState.humidity
            )
        }
        WeatherCard {
            WeatherInfoColumn(
                title = "Wind",
                firstDesc = "Speed",
                firstContent = weatherUiState.windSpeed,
                secondDesc = "Direction",
                secondContent = weatherUiState.windDirection
            )
        }
        Spacer(modifier = Modifier.weight(0.5f))
        TextButton(
            onClick = { weatherViewModel.onLocationPermissionGranted(application) },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier
                .fillMaxWidth()
                .padding(mediumPad)
        ) {
            Text(
                text = "Update location",
                fontSize = 24.sp,
                modifier = modifier.padding(smallPad)
            )
        }
    }

    if (weatherViewModel.locationPermissionDenied) {
        LocationPermissionDeniedSnackbar()
    }
}

@Composable
fun WeatherCard(
    paddingStart: Dp = dimensionResource(R.dimen.padding_medium),
    paddingEnd: Dp = dimensionResource(R.dimen.padding_medium),
    paddingTop: Dp = dimensionResource(R.dimen.padding_small),
    paddingBottom: Dp = dimensionResource(R.dimen.padding_small),
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = paddingStart,
                end = paddingEnd,
                top = paddingTop,
                bottom = paddingBottom
            ),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(content = content)
    }
}

@Composable
fun WeatherInfoColumn(
    title: String,
    firstDesc: String,
    firstContent: String,
    secondDesc: String,
    secondContent: String,
    modifier: Modifier = Modifier
) {
    val mediumPad = dimensionResource(R.dimen.padding_medium)
    Column(modifier = modifier.padding(bottom = 32.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            modifier = modifier.padding(start = mediumPad, end = mediumPad, top = mediumPad)
        )
        Row(modifier = modifier.padding(horizontal = mediumPad)) {
            Text(
                text = firstDesc,
                style = MaterialTheme.typography.headlineSmall,
                modifier = modifier.padding(end = 16.dp)
            )
            Text(
                text = firstContent,
                style = MaterialTheme.typography.headlineSmall
            )
        }
        Row(modifier = modifier.padding(horizontal = mediumPad)) {
            Text(
                text = secondDesc,
                style = MaterialTheme.typography.headlineSmall,
                modifier = modifier.padding(end = 16.dp)
            )
            Text(
                text = secondContent,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun LocationPermissionDeniedSnackbar() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Snackbar(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(24.dp)
        ) {
            Text("Location permission is required for the app to function properly.")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherLayoutPreview() {
    WeatherAppTheme(darkTheme = false) {
        Surface(modifier = Modifier.fillMaxSize()) {
            WeatherLayout()
        }
    }
}