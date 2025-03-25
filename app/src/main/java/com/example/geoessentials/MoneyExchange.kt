package com.example.geoessentials

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.geoessentials.data.Location
import kotlinx.coroutines.launch

@Composable
fun MoneyExchangeScreen(sourceLocation: Location, targetLocation: Location) {
    var sourceCurrency by remember { mutableStateOf(Currency("Unknown", "???")) }
    var targetCurrency by remember { mutableStateOf(Currency("Unknown", "???")) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(sourceLocation, targetLocation) {
        coroutineScope.launch {
            sourceCurrency = getCountryCurrency(sourceLocation.latitude, sourceLocation.longitude, sourceLocation) ?: Currency("Unknown", "???")
            targetCurrency = getCountryCurrency(targetLocation.latitude, targetLocation.longitude, targetLocation) ?: Currency("Unknown", "???")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.padding(16.dp).weight(0.5f)
            ) {
                Text(
                    text = "Currency: ${sourceCurrency.name} (${sourceCurrency.symbol})",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier.padding(16.dp).weight(0.5f)
            ) {
                Text(
                    text = "Currency: ${targetCurrency.name} (${targetCurrency.symbol})",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }
    }
}