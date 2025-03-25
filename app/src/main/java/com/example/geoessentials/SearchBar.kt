package com.example.geoessentials
import android.content.Context
import android.location.Geocoder
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SearchBar(onCitySelected: (String, Double, Double) -> Unit, isSource : Boolean) {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.clip(shape = RoundedCornerShape(15.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            value = searchText,
            textStyle = TextStyle(),
            maxLines = 2,
            onValueChange = { searchText = it },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                if (searchText.isNotBlank()) {  // Only search if input is not empty
                    coroutineScope.launch { getCityCoordinates(context, searchText, onCitySelected) }
                }
            }),
            modifier = Modifier
                .background(Color.White)
                .padding(8.dp)
                .fillMaxWidth(0.9f)
        )
    }
}

private suspend fun getCityCoordinates(context: Context, cityName: String, onCitySelected: (String, Double, Double) -> Unit) {
    val geocoder = Geocoder(context)
    val addressList = withContext(Dispatchers.IO) { geocoder.getFromLocationName(cityName, 1) }

    if (addressList.isNullOrEmpty()) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
        }
    } else {
        val location = addressList[0]
        onCitySelected(cityName, location.latitude, location.longitude)
    }
}