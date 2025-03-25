package com.example.geoessentials

import android.util.Log
import com.example.geoessentials.data.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenWeatherApi {
    @GET("reverse")
    suspend fun getLocations(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): List<ReverseResponse>
}

object OpenWeatherInstance {
    private const val BASE_URL = "https://api.openweathermap.org/geo/1.0/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: OpenWeatherApi by lazy {
        retrofit.create(OpenWeatherApi::class.java)
    }
}

interface RESTCountriesApi {
    @GET("v3.1/alpha/{code}")
    suspend fun getCountryInfo(@Path("code") code: String): List<CountryInfo>
}

object RESTCountriesInstance {
    private const val BASE_URL = "https://restcountries.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: RESTCountriesApi by lazy {
        retrofit.create(RESTCountriesApi::class.java)
    }
}

data class CountryInfo(
    val name: Name,
    val currencies: Map<String, Currency>
)

data class Name(
    val common: String,
    val official: String
)

data class Currency(
    val name: String,
    val symbol: String
)

suspend fun getCountryCurrency(lat: Double?, lon: Double?, location : Location): Currency? {
    if (lat == null || lon == null) return null

    return try {
        // Fetch location data from OpenWeather reverse API
        val locations = withContext(Dispatchers.IO) {
            OpenWeatherInstance.apiService.getLocations(lat, lon, BuildConfig.OPEN_WEATHER_API_KEY)
        }

        // Extract the country code from the first location
        val countryCode = locations.firstOrNull()?.country ?: return null
        location.countryCode = countryCode

        // Fetch country information from RESTCountries API
        val countryInfo = withContext(Dispatchers.IO) {
            RESTCountriesInstance.apiService.getCountryInfo(countryCode)
        }

        // Extract and return the currency information
        countryInfo.firstOrNull()?.currencies?.values?.firstOrNull()
    } catch (e: HttpException) {
        Log.e("CountryAPIService", "API call failed", e)
        null
    } catch (e: Exception) {
        Log.e("CountryAPIService", "Unexpected error", e)
        null
    }
}