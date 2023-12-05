package ch.simonpeier.weatherapp

data class WeatherUiState(
    val location: String = "Location",
    val weatherIcon: Int = R.drawable.ic_launcher_foreground,
    val date: String = "Date",
    val temperature: String = "-°",
    val airPressure: String = "",
    val humidity: String = "",
    val windSpeed: String = "",
    val windDirection: String = ""
)
