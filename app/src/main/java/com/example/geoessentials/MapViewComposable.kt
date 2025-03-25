package com.example.geoessentials

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.geoessentials.data.Location
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


const val POLYLINE_WIDTH = 1F
const val ICON_SIZE = 50

@Composable
fun MapViewComposable(
    latitude: Double?,
    longitude: Double?,
    mapViewState: MutableState<MapView?>,
    mapControllerState: MutableState<IMapController?>
) {
    AndroidView(
        factory = { context ->
            val mapView = MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
            }

            val controller: IMapController = mapView.controller

            val worldCenter = GeoPoint(50.0, 0.0)
            controller.setZoom(3.0)
            controller.animateTo(worldCenter)

            mapViewState.value = mapView
            mapControllerState.value = controller

            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
            locationOverlay.enableMyLocation()
            mapView.overlays.add(locationOverlay)

            mapView
        },
        update = { mapView ->
            if (latitude != null && longitude != null) {
                val geoPoint = GeoPoint(latitude, longitude)
                mapView.controller.animateTo(geoPoint)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

fun mapDrawables(mapController: MutableState<IMapController?>,
                 mapView : MutableState<MapView?>,
                 markers: SnapshotStateList<GeoPoint>,
                 aLocation : Location,
                 bLocation : Location,
                 isSource : Boolean
) {
    mapView.value?.overlays?.clear()
    if (aLocation.latitude == null || aLocation.longitude == null) return

    val aPoint = GeoPoint(aLocation.latitude!!, aLocation.longitude!!)

    markers.add(aPoint)

    if(isSource) {
        placeMarker(mapView, aPoint, aLocation, R.drawable.redmarker)
    } else {
        placeMarker(mapView, aPoint, aLocation, R.drawable.bluemarker)
    }

    if (bLocation.latitude != null || bLocation.longitude != null && isSource) {

        val bPoint = GeoPoint(bLocation.latitude!!, bLocation.longitude!!)

        val line = Polyline(mapView.value).apply {
            addPoint(aPoint)
            addPoint(bPoint)
            isGeodesic = true
            width = POLYLINE_WIDTH
        }

        mapView.value?.overlays?.add(line)

        val distance = aPoint.distanceToAsDouble(bPoint) / 1000.0 // Convert to kilometers
        val roundedDistance = "%.2f km".format(distance)

        // Add distance label overlay
        val distanceOverlay = DistanceOverlay(aPoint, bPoint, roundedDistance)
        mapView.value?.overlays?.add(distanceOverlay)

        markers.add(bPoint)

        if(isSource) {
            placeMarker(mapView, bPoint, bLocation, R.drawable.bluemarker)
        } else {
            placeMarker(mapView, bPoint, bLocation, R.drawable.redmarker)
        }



        adjustZoom(mapController.value!!, mapView.value!!, aPoint, bPoint)
    } else {
        mapController.value?.setZoom(10.0)
        mapController.value?.animateTo(aPoint)
    }
}

private fun placeMarker(mapView: MutableState<MapView?>, point : GeoPoint, location: Location, iconDrawable: Int) {
    val drawable = ContextCompat.getDrawable(mapView.value!!.context, iconDrawable)

    drawable?.let {
        val scaledDrawable = Bitmap.createScaledBitmap(
            (it as android.graphics.drawable.BitmapDrawable).bitmap,
            ICON_SIZE,
            ICON_SIZE,
            true
        )

        val markerB = Marker(mapView.value).apply {
            position = point
            title = location.searchName
            icon = BitmapDrawable(mapView.value!!.context.resources, scaledDrawable) // Set resized icon
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM) // Center the icon
        }

        mapView.value?.overlays?.add(markerB)
    }
}

class DistanceOverlay(
    private val start: GeoPoint,
    private val end: GeoPoint,
    private val text: String
) : Overlay() {
    private val textPaint = Paint().apply {
        color = android.graphics.Color.GREEN // Main text color
        textSize = 30f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val borderPaint = Paint().apply {
        color = android.graphics.Color.BLACK // Outline color
        textSize = 30f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 8f // Thickness of the border
    }

    override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
        if (shadow) return

        val startPoint = mapView.projection.toPixels(start, null)
        val endPoint = mapView.projection.toPixels(end, null)
        val midX = (startPoint.x + endPoint.x) / 2
        val midY = (startPoint.y + endPoint.y) / 2

        var angle = Math.toDegrees(
            kotlin.math.atan2((endPoint.y - startPoint.y).toDouble(), (endPoint.x - startPoint.x).toDouble())
        ).toFloat()

        if (angle in 90f..270f) {
            angle -= 180f

        } else if (angle in -270f..-90f){
            angle += 180f
        }


        canvas.save()
        canvas.rotate(angle, midX.toFloat(), midY.toFloat())

        // Draw border first (thicker black outline)
        canvas.drawText(text, midX.toFloat(), midY.toFloat(), borderPaint)
        // Draw main text on top (white)
        canvas.drawText(text, midX.toFloat(), midY.toFloat(), textPaint)

        canvas.restore()
    }
}

