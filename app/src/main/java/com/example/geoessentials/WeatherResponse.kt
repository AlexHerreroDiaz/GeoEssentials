package com.example.geoessentials

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.geoessentials.SearchBar
import com.example.geoessentials.MapViewComposable
import com.example.geoessentials.adjustZoom
import com.example.geoessentials.calculateMidPoint
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.api.IMapController
import org.osmdroid.views.overlay.Marker
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview

data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val rain: Rain?,
    val clouds: Clouds,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

data class Coord(val lon: Double, val lat: Double)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int,
    val sea_level: Int,
    val grnd_level: Int
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

data class Rain(
    val `1h`: Double?
)

data class Clouds(val all: Int)

data class Sys(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)


@Composable
fun WeatherColumn(
    temperature: String,
    feelsLike: String,
    humidity: String,
    weatherIcon: Int,
    description: String,
    sunrise: Long?,
    sunset: Long?,
    timezone: Int?
) {
    Column(
        modifier = Modifier
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = weatherIcon),
            contentDescription = "Weather Icon",
            modifier = Modifier
                .size(100.dp)
                .padding(vertical = 8.dp)
        )
        Text(text = description, color = Color.White)
        Text(text = "Temperature: $temperature", color = Color.White)
        Text(text = "Feels Like: $feelsLike", color = Color.White)
        Text(text = "Humidity: $humidity", color = Color.White)
        if (sunrise != null) {
            Text(text = "Sunrise: ${epochToDate(sunrise, timezone!!)}", color = Color.White)
        } else {
            Text(text = "Sunrise: N/A", color = Color.White)
        }
        if (sunset != null) {
            Text(text = "Sunset: ${epochToDate(sunset, timezone!!)}", color = Color.White)
        } else {
            Text(text = "Sunset: N/A", color = Color.White)
        }
    }
}

@Composable
fun WeatherInfoCard(
    sourceWeather: WeatherResponse?,
    targetWeather: WeatherResponse?
) {
    // source location weather parameters
    val sourceTemp = sourceWeather?.main?.temp?.let { String.format("%.2f", it - 273.15) } ?: "N/A"
    val sourceFeelsLike = sourceWeather?.main?.feels_like?.let { String.format("%.2f", it - 273.15) } ?: "N/A"
    val sourceIcon = sourceWeather?.weather?.firstOrNull()?.icon?.let { weatherDrawables[it] } ?: R.drawable.empty
    val sourceDescription = sourceWeather?.weather?.firstOrNull()?.description ?: "N/A"
    val sourceHumidity = sourceWeather?.main?.humidity?.toString() ?: "N/A"
    val sourceSunrise = sourceWeather?.sys?.sunrise?.let { it * 1000 } ?: null
    val sourceSunset = sourceWeather?.sys?.sunset?.let { it * 1000 } ?: null

    // target location weather parameters
    val targetTemp = targetWeather?.main?.temp?.let { String.format("%.2f", it - 273.15) } ?: "N/A"
    val targetFeelsLike = targetWeather?.main?.feels_like?.let { String.format("%.2f", it - 273.15) } ?: "N/A"
    val targetIcon = targetWeather?.weather?.firstOrNull()?.icon?.let { weatherDrawables[it] } ?: R.drawable.empty
    val targetDescription = targetWeather?.weather?.firstOrNull()?.description ?: "N/A"
    val targetHumidity = targetWeather?.main?.humidity?.toString() ?: "N/A"
    val targetSunrise = targetWeather?.sys?.sunrise?.let { it * 1000 } ?: null
    val targetSunset = targetWeather?.sys?.sunset?.let { it * 1000 } ?: null


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherColumn(
                temperature = "${sourceTemp}°C",
                feelsLike = "${sourceFeelsLike}°C",
                humidity = "${sourceHumidity}%",
                weatherIcon = sourceIcon,
                description = sourceDescription,
                sunrise = sourceSunrise,
                sunset = sourceSunset,
                timezone = sourceWeather?.timezone
            )
            WeatherColumn(
                temperature = "${targetTemp}°C",
                feelsLike = "${targetFeelsLike}°C",
                humidity = "${targetHumidity}%",
                weatherIcon = targetIcon,
                description = targetDescription,
                sunrise = targetSunrise,
                sunset = targetSunset,
                timezone = targetWeather?.timezone
            )
        }
    }
}