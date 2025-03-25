
package com.example.geoessentials

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.api.IMapController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.geoessentials.data.Location

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun Widgets_Preview() {

    var sourceLocation by remember { mutableStateOf(Location(null, null, "", "")) }
    var targetLocation by remember { mutableStateOf(Location(null, null, "", "")) }

    // Remember MapView and Controller
    val mapView = remember { mutableStateOf<MapView?>(null) }
    val mapController = remember { mutableStateOf<IMapController?>(null) }

    val markers = remember { mutableStateListOf<GeoPoint>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Blue)
            .clip(shape = RoundedCornerShape(15.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        // Map Area
        Box(
            modifier = Modifier
                .background(color = Color(179, 255, 237, 255))
                .weight(0.2f)
                .fillMaxWidth()
        ) {
        }

        // Header with Search Bar
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp))
                .background(color = Color(164, 207, 215, 255))
                .weight(0.1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Row {
                Column(modifier = Modifier
                    .weight(1f)
                    .padding(top = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    SearchBar ({ city, lat, lon ->
                        sourceLocation = Location(lat, lon, city) // Create a new instance
                    }, isSource = true)

                    if (sourceLocation.searchName.isNotEmpty()) {
                        Text("Source: ${sourceLocation.searchName}", color = Color.White)

                    } else {
                        Text("Source: ", color = Color.White)
                    }
                }

                Column(modifier = Modifier
                    .weight(1f)
                    .padding(top = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    SearchBar ({ city, lat, lon ->
                        targetLocation = Location(lat, lon, city) // Create a new instance
                    }, isSource = false)

                    if (targetLocation.searchName.isNotEmpty()) {
                        Text("Target: ${targetLocation.searchName}", color = Color.White)

                    } else {
                        Text("Target: ", color = Color.White)
                    }
                }
            }
        }

        // Weather Area
        Box(
            modifier = Modifier
                .background(color = Color(76, 171, 255, 255))
                .weight(0.2f) // Takes proportional space
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title at the Top
                Text(
                    text = "Weather Information",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Row with Two Columns: Source & Target Weather
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Source Weather Column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(), // Use full height
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Source", color = Color.White, style = MaterialTheme.typography.bodyLarge)

                        // Weather Icon
                        Image(
                            painter = painterResource(id = R.drawable.redmarker), // Replace with your drawable resource
                            contentDescription = "Source Weather Icon",
                            modifier = Modifier
                                .size(60.dp) // Adjust size as needed
                                .padding(vertical = 8.dp)
                        )

                        Text(text = "Temperature: 25°C", color = Color.White)
                        Text(text = "Humidity: 60%", color = Color.White)
                    }

                    // Target Weather Column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(), // Use full height
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Target", color = Color.White, style = MaterialTheme.typography.bodyLarge)

                        // Weather Icon
                        Image(
                            painter = painterResource(id = R.drawable.bluemarker), // Replace with your drawable resource
                            contentDescription = "Target Weather Icon",
                            modifier = Modifier
                                .size(60.dp) // Adjust size as needed
                                .padding(vertical = 8.dp)
                        )

                        Text(text = "Temperature: 22°C", color = Color.White)
                        Text(text = "Humidity: 55%", color = Color.White)
                    }
                }
            }
        }


        // Time Zones
        Box(
            modifier = Modifier
                .background(color = Color(88, 113, 255, 255))
                .weight(0.1f)
                .fillMaxWidth()
        )
        {
            Column(
                modifier = Modifier.fillMaxSize().clip(shape = RoundedCornerShape(15.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title at the Top
                Text(
                    text = "Weather Information",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Row with Two Columns: Source & Target Weather
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Source Weather Column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(), // Use full height
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "17:06", color = Color.White, fontSize = 30.sp)
                        Text(text = "Monday, 2025/03/19", color = Color.White)
                    }

                    // Target Weather Column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(), // Use full height
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "14:06", color = Color.White, fontSize = 30.sp)
                        Text(text = "Monday, 2025/03/19", color = Color.White)
                    }
                }
            }
        }

        // Money Exchange
        Box(
            modifier = Modifier
                .background(color = Color(63, 81, 181, 255))
                .weight(0.15f)
                .fillMaxWidth()
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getTimeInTimeZone(timezone : Int): Pair<String?, String?> {
    val nowUtc = Instant.now()
    val zoneOffset = ZoneId.ofOffset("UTC", java.time.ZoneOffset.ofTotalSeconds(timezone))
    val zonedDateTime = nowUtc.atZone(zoneOffset)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    val dayFormatter = DateTimeFormatter.ofPattern("EEEE, yyyy/MM/dd", Locale.getDefault())
    return  Pair(zonedDateTime.format(timeFormatter), zonedDateTime.format(dayFormatter))
}

@Preview
@Composable
fun SimpleComposablePreview() {
    Widgets_Preview()
}