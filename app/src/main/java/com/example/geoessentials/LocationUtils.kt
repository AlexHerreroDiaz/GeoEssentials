package com.example.geoessentials

import org.osmdroid.util.GeoPoint
import kotlin.math.*

fun calculateMidPoint(lat1: Double, lon1: Double, lat2: Double, lon2: Double): GeoPoint {
    val lat1Rad = Math.toRadians(lat1)
    val lon1Rad = Math.toRadians(lon1)
    val lat2Rad = Math.toRadians(lat2)
    val lon2Rad = Math.toRadians(lon2)

    val dLon = lon2Rad - lon1Rad
    val bx = cos(lat2Rad) * cos(dLon)
    val by = cos(lat2Rad) * sin(dLon)

    val midLat = atan2(
        sin(lat1Rad) + sin(lat2Rad),
        sqrt((cos(lat1Rad) + bx) * (cos(lat1Rad) + bx) + by * by)
    )
    val midLon = lon1Rad + atan2(by, cos(lat1Rad) + bx)

    return GeoPoint(Math.toDegrees(midLat), Math.toDegrees(midLon))
}

fun adjustZoom(mapController: org.osmdroid.api.IMapController, mapView: org.osmdroid.views.MapView, point1: GeoPoint, point2: GeoPoint): GeoPoint{
    val distance = point1.distanceToAsDouble(point2)
    val zoomLevel = when {
        distance > 5000000 -> 3.0
        distance > 3000000 -> 3.5
        distance > 2000000 -> 4.0
        distance > 1500000 -> 4.5
        distance > 1000000 -> 5.0
        distance > 750000 -> 5.5
        distance > 500000 -> 6.0
        distance > 300000 -> 6.5
        distance > 200000 -> 7.0
        distance > 150000 -> 7.5
        distance > 100000 -> 8.0
        distance > 75000 -> 8.5
        distance > 50000 -> 9.0
        distance > 35000 -> 9.5
        distance > 25000 -> 10.0
        distance > 15000 -> 10.5
        distance > 10000 -> 11.0
        distance > 5000 -> 11.5
        else -> 12.0
    }

    val midPoint = calculateMidPoint(point1.latitude, point1.longitude, point2.latitude, point2.longitude)

    mapController.setZoom(zoomLevel)
    mapController.animateTo(midPoint)

    return midPoint
}
