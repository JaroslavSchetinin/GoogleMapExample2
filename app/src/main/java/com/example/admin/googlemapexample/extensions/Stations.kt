package com.example.admin.googlemapexample.extensions

import com.example.admin.googlemapexample.model.Station
import com.google.android.gms.maps.model.LatLng

fun Station.getLatLng(): LatLng = LatLng(this.latitude?.toDouble()
        ?: 0.0, this.longitude?.toDouble() ?: 0.0)