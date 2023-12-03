package ch.simonpeier.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.simonpeier.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherLayout()
                }
            }
        }
    }
}

@Composable
fun WeatherLayout(modifier: Modifier = Modifier, weatherViewModel: WeatherViewModel = viewModel()) {
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

@Preview(showBackground = true)
@Composable
fun WeatherLayoutPreview() {
    WeatherAppTheme {
        WeatherLayout()
    }
}