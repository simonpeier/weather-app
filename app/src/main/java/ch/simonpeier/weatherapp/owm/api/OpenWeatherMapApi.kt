package ch.simonpeier.weatherapp.owm.api

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapApi {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") latidude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): OwmResponse
}