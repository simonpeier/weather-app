package ch.simonpeier.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    WeatherLayout("Android")
                }
            }
        }
    }
}

@Composable
fun WeatherLayout(name: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(48.dp)
    ) {
        Text(text = "Location", fontSize = 48.sp)
        Text(text = "Date", fontSize = 32.sp, modifier = Modifier.padding(bottom = 40.dp))
        Text(text = "-°", fontSize = 48.sp, modifier = Modifier.padding(bottom = 40.dp))
        WeatherInfoCard(title = "Air", firstContent = "Pressure", secondContent = "Humidity")
        WeatherInfoCard(title = "Wind", firstContent = "Speed", secondContent = "Direction")
    }
}

@Composable
fun WeatherInfoCard(
    title: String,
    firstContent: String,
    secondContent: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(bottom = 32.dp)) {
        Text(text = title, fontSize = 32.sp)
        Text(text = firstContent, fontSize = 24.sp)
        Text(text = secondContent, fontSize = 24.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherLayoutPreview() {
    WeatherAppTheme {
        WeatherLayout("Android")
    }
}