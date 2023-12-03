package ch.simonpeier.weatherapp.owm.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapApi {
    @GET("weather")
    fun getCurrentWeather(
        @Query("lat") latidude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Call<OwmResponse>
}