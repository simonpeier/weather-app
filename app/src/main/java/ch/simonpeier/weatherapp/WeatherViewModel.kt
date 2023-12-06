package ch.simonpeier.weatherapp

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
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

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val _locationPermissionDenied = mutableStateOf(false)
    val locationPermissionDenied: Boolean
        get() = _locationPermissionDenied.value

    fun fetchData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val weather = weatherService.getWeather(latitude, longitude)
                val location = weather.name.split(Regex("/"), 2)
                val calendar = Calendar.getInstance()
                val dateOnly = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN)
                val windDirection = weather.wind.deg.let { getWindDirection(it) }
                val icon = getResourceId(weather.weather[0].icon)

                _uiState.update { currentState ->
                    currentState.copy(
                        location = location[0],
                        weatherIcon = icon,
                        date = dateOnly.format(calendar.time),
                        temperature = "${weather.main.temp.roundToInt()}°",
                        airPressure = "${weather.main.pressure} hPa",
                        humidity = "${weather.main.humidity}%",
                        windSpeed = "${(weather.wind.speed * 3.6).roundToInt()} km/h",
                        windDirection = windDirection
                    )
                }

            } catch (e: Exception) {
                Log.e("WeatherViewModel", e.message.toString())
            }
        }
    }

    fun onLocationPermissionGranted(application: Application) {
        _locationPermissionDenied.value = false
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
        }
        requestLocationUpdates()
    }

    fun onLocationPermissionDenied() {
        _locationPermissionDenied.value = true
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val lastLocation: Location = locationResult.lastLocation
                fetchData(lastLocation.latitude, lastLocation.longitude)
            }
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

        fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
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

    private fun getResourceId(key: String): Int {
        return when (key) {
            "01d" -> R.drawable.icon_01d
            "01n" -> R.drawable.icon_01n
            "02d" -> R.drawable.icon_02d
            "02n" -> R.drawable.icon_02n
            "03d" -> R.drawable.icon_03d
            "03n" -> R.drawable.icon_03n
            "04d" -> R.drawable.icon_04d
            "04n" -> R.drawable.icon_04n
            "09d" -> R.drawable.icon_09d
            "09n" -> R.drawable.icon_09n
            "10d" -> R.drawable.icon_10d
            "10n" -> R.drawable.icon_10n
            "11d" -> R.drawable.icon_11d
            "11n" -> R.drawable.icon_11n
            "13d" -> R.drawable.icon_13d
            "13n" -> R.drawable.icon_13n
            "50d" -> R.drawable.icon_50d
            "50n" -> R.drawable.icon_50n
            else -> R.drawable.ic_launcher_foreground // Use a default image if the key is not found
        }
    }
}