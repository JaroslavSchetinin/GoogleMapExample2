package com.example.admin.googlemapexample.extensions

import com.google.android.gms.maps.model.LatLng
private val EARTH_RADIUS = 6371000.0

fun Pair<LatLng, LatLng>.getDistance(): Int {
    val dLat = Math.toRadians(second.latitude - first.latitude)
    val dLng = Math.toRadians(second.longitude - first.longitude)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(first.latitude)) *
            Math.cos(Math.toRadians(second.latitude)) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return (EARTH_RADIUS * c).toInt()
}