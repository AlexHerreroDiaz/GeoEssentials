package com.example.geoessentials

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.geoessentials.data.Location

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.api.IMapController
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Widgets() {
    var sourceLocation by remember { mutableStateOf(Location(null, null, "", "")) }
    var targetLocation by remember { mutableStateOf(Location(null, null, "", "")) }

    val sourceWeatherResponse = remember { mutableStateOf<WeatherResponse?>(null) }
    val targetWeatherResponse = remember { mutableStateOf<WeatherResponse?>(null) }

    // Remember MapView and Controller
    val mapView = remember { mutableStateOf<MapView?>(null) }
    val mapController = remember { mutableStateOf<IMapController?>(null) }

    val markers = remember { mutableStateListOf<GeoPoint>() }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color(49, 85, 94, 255))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(30, 56, 61, 255)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Map Area
            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxWidth()
            ) {
                MapViewComposable(
                    latitude = sourceLocation.latitude,
                    longitude = sourceLocation.longitude,
                    mapViewState = mapView,
                    mapControllerState = mapController
                )
            }

            // Header with Search Bar
            Box(
                modifier = Modifier
                    .background(color = Color(164, 207, 215, 255))
                    .weight(0.1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SearchBar({ city, lat, lon ->
                            sourceLocation = Location(lat, lon, city)
                        }, isSource = true)

                        if (sourceLocation.searchName.isNotEmpty()) {
                            LaunchedEffect(sourceLocation, targetLocation) {
                                mapDrawables(
                                    mapController,
                                    mapView,
                                    markers,
                                    sourceLocation,
                                    targetLocation,
                                    true
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SearchBar({ city, lat, lon ->
                            targetLocation = Location(lat, lon, city)
                        }, isSource = false)

                        if (targetLocation.searchName.isNotEmpty()) {
                            LaunchedEffect(sourceLocation, targetLocation) {
                                mapDrawables(
                                    mapController,
                                    mapView,
                                    markers,
                                    targetLocation,
                                    sourceLocation,
                                    false
                                )
                            }
                        }
                    }
                }
            }

            // Info Section
            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Location Area
                Box(
                    modifier = Modifier
                        .background(color = Color(78, 173, 192, 255))
                        .fillMaxWidth()
                        .height(75.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Country: ${sourceLocation.countryCode.ifEmpty { "N/A" }}\n" +
                                    "Flag: ${countryCodeToEmojiFlag(sourceLocation.countryCode.ifEmpty { "N/A" })}",
                            color = Color.White,
                            modifier = Modifier
                                .padding(16.dp)
                        )

                        Text(
                            text = "Country: ${targetLocation.countryCode.ifEmpty { "N/A" }}\n" +
                                    "Flag: ${countryCodeToEmojiFlag(targetLocation.countryCode.ifEmpty { "N/A" })}",
                            color = Color.White,
                            modifier = Modifier
                                .padding(16.dp)
                        )
                    }
                }

                Box(modifier = Modifier.background(Color(0, 0, 0, 49)).fillMaxWidth().height(10.dp))
                Spacer(modifier = Modifier.height(10.dp))

                // Weather Area
                Box(
                    modifier = Modifier
                        .background(color = Color(49, 85, 94, 255))
                        .height(300.dp)
                        .fillMaxWidth()
                ) {
                    // Set red marker icon image at top left corner of the Weather Area
                    Image(
                        painter = painterResource(id = R.drawable.redmarker),
                        contentDescription = "Red Marker",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(30.dp)
                            .align(Alignment.TopStart)
                    )
                    // Set blue marker icon image at top right corner of the Weather Area
                    Image(
                        painter = painterResource(id = R.drawable.bluemarker),
                        contentDescription = "Red Marker",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(30.dp)
                            .align(Alignment.TopEnd)
                    )

                    WeatherScreen(
                        sourceWeatherResponse,
                        sourceLocation,
                        targetWeatherResponse,
                        targetLocation
                    )
                }

                Box(modifier = Modifier.background(Color(0, 0, 0, 49)).fillMaxWidth().height(10.dp))
                Spacer(modifier = Modifier.height(10.dp))

                // Time Zones
                Box(
                    modifier = Modifier
                        .background(color = Color(78, 173, 192, 255))
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(15.dp))
                ) {
                    TimezoneComparisonScreen(
                        sourceWeatherResponse.value,
                        targetWeatherResponse.value
                    )
                }

                Box(modifier = Modifier.background(Color(0, 0, 0, 49)).fillMaxWidth().height(10.dp))
                Spacer(modifier = Modifier.height(10.dp))

                // Money Exchange
                Box(
                    modifier = Modifier
                        .background(color = Color(49, 85, 94, 255))
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    // Display Money Exchange Rates for both locations side by side
                    MoneyExchangeScreen(
                        sourceLocation,
                        targetLocation
                    )
                }

                Box(modifier = Modifier.background(Color(0, 0, 0, 49)).fillMaxWidth().height(10.dp))
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

fun Modifier.leftBorder(
    color: Color,
    width: Float,
) = this.drawWithContent {
    drawContent()
    drawLine(
        color = color,
        start = Offset(0f, 0f),
        end = Offset(0f, size.height),
        strokeWidth = width,
    )
}

fun Modifier.rightBorder(
    color: Color,
    width: Float,
) = this.drawWithContent {
    drawContent()
    drawLine(
        color = color,
        start = Offset(size.width, 0f),
        end = Offset(size.width, size.height),
        strokeWidth = width,
    )
}

fun countryCodeToEmojiFlag(countryCode: String): String {
    if (countryCode == "N/A") {
        return "N/A"
    }
    return countryCode
        .uppercase(Locale.US)
        .map { char ->
            Character.codePointAt("$char", 0) - 0x41 + 0x1F1E6
        }
        .map { codePoint ->
            Character.toChars(codePoint)
        }
        .joinToString(separator = "") { charArray ->
            String(charArray)
        }
}

