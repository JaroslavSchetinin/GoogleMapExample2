package com.example.admin.googlemapexample

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.admin.googlemapexample.extensions.getDistance
import com.example.admin.googlemapexample.model.Stations
import com.google.android.gms.maps.model.LatLng

class RecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val stationTitle = itemView.findViewById<TextView>(R.id.station_title_text_view)
    private val distanceToMeText = itemView.findViewById<TextView>(R.id.station_distance_text_view)

    fun bind(station: Stations) {
        val distanseString = "${station.distanceToMe} meters"
        stationTitle.text = station.name
        distanceToMeText.text = distanseString
    }


}
