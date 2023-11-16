package ch.simonpeier.weatherapp.owm.api

data class Wind(
    val deg: Int,
    val gust: Double,
    val speed: Double
)