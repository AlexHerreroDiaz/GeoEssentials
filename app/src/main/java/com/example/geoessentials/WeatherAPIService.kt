package com.example.geoessentials

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.widget.TextClock
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.geoessentials.data.Location
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

val weatherDrawables = mapOf(
    "01d" to R.drawable.clear_sky,
    "01n" to R.drawable.clear_sky_night,
    "02d" to R.drawable.few_clouds,
    "02n" to R.drawable.few_clouds_night,
    "03d" to R.drawable.scattered_clouds,
    "03n" to R.drawable.scattered_clouds,
    "04d" to R.drawable.broken_clouds,
    "04n" to R.drawable.broken_clouds,
    "09d" to R.drawable.shower_rain,
    "09n" to R.drawable.shower_rain,
    "10d" to R.drawable.rain,
    "10n" to R.drawable.rain_night,
    "11d" to R.drawable.thunderstorm,
    "11n" to R.drawable.thunderstorm,
    "13d" to R.drawable.snow,
    "13n" to R.drawable.snow,
    "50d" to R.drawable.mist,
    "50n" to R.drawable.mist,
)

interface WeatherApiService {
    @GET("weather")
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Call<WeatherResponse>
}

object RetrofitInstance {

    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}

@Composable
fun FetchWeatherData(
    sourceLat: Double?,
    sourceLon: Double?,
    targetLat: Double?,
    targetLon: Double?,
    onSourceWeatherFetched: (WeatherResponse?) -> Unit,
    onTargetWeatherFetched: (WeatherResponse?) -> Unit
) {
    val apiKey = BuildConfig.OPEN_WEATHER_API_KEY

    if (sourceLat == null || sourceLon == null) {
        onSourceWeatherFetched(null)
    } else {
        // Fetch source weather if it is not already fetched
        LaunchedEffect(sourceLat, sourceLon) {
            RetrofitInstance.apiService.getWeather(sourceLat, sourceLon, apiKey).enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        onSourceWeatherFetched(response.body())
                    } else {
                        Log.e("WeatherScreen", "Failed to load source weather")
                        onSourceWeatherFetched(null)
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("WeatherScreen", "Failed to load source weather", t)
                    onSourceWeatherFetched(null)
                }
            })
        }
    }

    if (targetLat == null || targetLon == null) {
        onTargetWeatherFetched(null)
    } else {
        // Fetch target weather if it is not already fetched
        LaunchedEffect(targetLat, targetLon) {
            RetrofitInstance.apiService.getWeather(targetLat, targetLon, apiKey).enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        onTargetWeatherFetched(response.body())
                    } else {
                        Log.e("WeatherScreen", "Failed to load target weather")
                        onTargetWeatherFetched(null)
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("WeatherScreen", "Failed to load target weather", t)
                    onTargetWeatherFetched(null)
                }
            })
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun WeatherScreen(
    sourceWeatherResponse: MutableState<WeatherResponse?>?,
    source: Location?,
    targetWeatherResponse: MutableState<WeatherResponse?>?,
    target: Location?
) {
    FetchWeatherData(
        sourceLat = source?.latitude,
        sourceLon = source?.longitude,
        targetLat = target?.latitude,
        targetLon = target?.longitude,
        onSourceWeatherFetched = { sourceWeatherResponse?.value = it },
        onTargetWeatherFetched = { targetWeatherResponse?.value = it }
    )

    val sourceWeather = sourceWeatherResponse?.value
    val targetWeather = targetWeatherResponse?.value

    WeatherInfoCard(
        sourceWeather = sourceWeather,
        targetWeather = targetWeather
    )
}

@Composable
fun TimezoneComparisonScreen(source: WeatherResponse?, target: WeatherResponse?) {
    if (source == null && target == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimeDisplay(null, null)
                TimeDisplay(null, null)
            }
        }
    } else {
        var time1 by remember { mutableStateOf<String?>(null) }
        var time2 by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(source?.timezone) {
            if (source != null) {
                while (true) {
                    time1 = getTimeForOffset(source.timezone)
                    delay(1000)
                }
            }
        }

        LaunchedEffect(target?.timezone) {
            if (target != null) {
                while (true) {
                    time2 = getTimeForOffset(target.timezone)
                    delay(1000)
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimeDisplay(time1, source?.let { getDateForOffset(it.timezone) })
                TimeDisplay(time2, target?.let { getDateForOffset(it.timezone) })
            }
        }
    }
}

@Composable
fun TimeDisplay(time: String?, date: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)) {
        if(time == null || date == null) {
            Text(text = "--:--:--", fontSize = 24.sp, textAlign = TextAlign.Center, color = Color.White)
            Text(text = "No location", fontSize = 18.sp, textAlign = TextAlign.Center, color = Color.White)
        } else {
            Text(text = time, fontSize = 24.sp, textAlign = TextAlign.Center, color = Color.White)
            Text(text = date, fontSize = 18.sp, textAlign = TextAlign.Center, color = Color.White)
        }
    }
}

// Convert seconds from UTC into a valid timezone ID and get current time
fun getTimeForOffset(secondsFromUTC: Int): String {
    val offsetMillis = secondsFromUTC * 1000
    val availableIDs = TimeZone.getAvailableIDs(offsetMillis)
    val timeZoneID = availableIDs.firstOrNull() ?: "UTC"

    val sdfTime = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
    sdfTime.timeZone = TimeZone.getTimeZone(timeZoneID)
    return sdfTime.format(Date())
}

// function to convert epoch to time given a timezone in seconds
fun epochToDate(epoch: Long, timezone: Int): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val date = Date(epoch + timezone * 1000)
    return sdf.format(date)
}

// Convert seconds from UTC into a valid timezone ID and get current date
fun getDateForOffset(secondsFromUTC: Int): String {
    val offsetMillis = secondsFromUTC * 1000
    val availableIDs = TimeZone.getAvailableIDs(offsetMillis)
    val timeZoneID = availableIDs.firstOrNull() ?: "UTC"

    val sdfDate = SimpleDateFormat("EEEE, yyyy/MM/dd", Locale.ENGLISH)
    sdfDate.timeZone = TimeZone.getTimeZone(timeZoneID)
    return sdfDate.format(Date())
}

// Convert "UTC+09:00" format into a timezone and get current time
fun getTimeForUTCString(utcString: String): String {
    val regex = """UTC([+-])(\d{2}):(\d{2})""".toRegex()
    val match = regex.matchEntire(utcString)

    if (match != null) {
        val (sign, hours, minutes) = match.destructured
        val totalOffsetSeconds =
            (hours.toInt() * 3600 + minutes.toInt() * 60) * if (sign == "-") -1 else 1

        return getTimeForOffset(totalOffsetSeconds)
    }

    return "Invalid Timezone"
}