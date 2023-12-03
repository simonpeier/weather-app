package ch.simonpeier.weatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

class WeatherViewModel(private val weatherService: WeatherService = WeatherService()) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun fetchData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val weather = weatherService.getWeather(latitude, longitude)
                val location = weather.name.split(Regex("/"), 2)
                val calendar = Calendar.getInstance()
                val dateOnly = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN)
                val windDirection = (weather.wind.deg?.let { getWindDirection(it) } ?: "")

                _uiState.update { currentState ->
                    currentState.copy(
                        location = location[0],
                        date = dateOnly.format(calendar.time),
                        temperature = "${weather.main.temp.roundToInt()}°",
                        airPressure = "${weather.main.pressure} hPa",
                        humidity = "${weather.main.humidity}%",
                        windSpeed = "${(weather.wind.speed * 3.6).roundToInt()} km/h",
                        windDirection = windDirection
                    )
                }

            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    private fun getWindDirection(degree: Int): String {
        return when {
            degree > 337 || degree < 22.5 -> "N"
            degree > 292 -> "NW"
            degree > 247 -> "W"
            degree > 202 -> "SW"
            degree > 157 -> "S"
            degree > 112 -> "SE"
            degree > 67 -> "E"
            degree > 22.5 -> "NE"
            else -> "invalid"
        }
    }
}