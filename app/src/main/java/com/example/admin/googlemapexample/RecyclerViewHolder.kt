package com.example.admin.googlemapexample

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.budiyev.android.circularprogressbar.CircularProgressBar
import com.example.admin.googlemapexample.model.Station

class RecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val stationTitle = itemView.findViewById<TextView>(R.id.station_title_text_view)
    private val distanceToMeText = itemView.findViewById<TextView>(R.id.station_distance_text_view)
    private val leftProgresBar = itemView.findViewById<CircularProgressBar>(R.id.progress_bar_left)
    private val stationInfoText = itemView.findViewById<TextView>(R.id.station_info_text_view)


    fun bind(station: Station) {
        val stationInfo = "${station.free_bikes} bikes, ${station.empty_slots} parking slots."
        val distanceString = "${station.distanceToMe} meters"
        val bikesSlotsSum = station.free_bikes?.toFloat()?.let { station.empty_slots?.toFloat()?.plus(it) }
        stationTitle.text = station.name
        distanceToMeText.text = distanceString
        leftProgresBar.progress = 100*(station.free_bikes?.toFloat()?.div(bikesSlotsSum!!) ?: 0F)
        stationInfoText.text = stationInfo
    }


}
