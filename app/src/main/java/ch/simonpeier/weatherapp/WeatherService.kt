package ch.simonpeier.weatherapp

import ch.simonpeier.weatherapp.owm.api.OpenWeatherMapApi
import ch.simonpeier.weatherapp.owm.api.OwmResponse
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class WeatherService {
    private val openWeatherMapApi: OpenWeatherMapApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
                )
            ).build()

        openWeatherMapApi = retrofit.create(OpenWeatherMapApi::class.java)
    }

    suspend fun getWeather(latitude: Double, longitude: Double): OwmResponse {
        return withContext(Dispatchers.IO) {
            // Use enqueue for asynchronous call
            val call = openWeatherMapApi.getCurrentWeather(latitude, longitude, API_KEY, UNITS)

            return@withContext suspendCoroutine { continuation ->
                call.enqueue(object : Callback<OwmResponse> {
                    override fun onResponse(call: Call<OwmResponse>, response: Response<OwmResponse>) {
                        if (response.isSuccessful) {
                            continuation.resume(response.body()!!)
                        } else {
                            continuation.resumeWithException(Exception("Failed to fetch data"))
                        }
                    }

                    override fun onFailure(call: Call<OwmResponse>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
            }
        }
    }

    companion object {
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        private const val UNITS = "metric"
        private const val API_KEY = "cd91eaa4c63ffe5597cec2da4f6c7efe"
    }
}