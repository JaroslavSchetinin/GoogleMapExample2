package com.example.admin.googlemapexample

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.admin.googlemapexample.extensions.getDistance
import com.example.admin.googlemapexample.extensions.getLatLng
import com.example.admin.googlemapexample.model.Stations
import com.google.android.gms.maps.model.LatLng

class StationListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.station_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        var totalStationsText = view.findViewById<TextView>(R.id.total_stations_text_view)
        var totalBikesText = view.findViewById<TextView>(R.id.total_bikes_text_view)
        var totalSlotsText = view.findViewById<TextView>(R.id.total_slots_text_view)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = StationRecyclerAdapter()
        recyclerView.adapter = adapter


        val stations = MainActivity.bikeStations?.second
        totalStationsText.text = stations?.size.toString()
        totalBikesText.text = stations?.sumBy{it.free_bikes!!.toInt()}.toString()
        totalSlotsText.text = stations?.sumBy{it.empty_slots!!.toInt()}.toString()

        stations?.forEach { it.distanceToMe = calculateDistance(it) }
        stations?.let { adapter.setItems(it.sortedBy { it.distanceToMe }) }
        adapter.setOnRecyclerClicked(object : StationRecyclerAdapter.OnRecyclerClicked {
            override fun onClick(stations: Stations) {
                Toast.makeText(context, stations.name, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun calculateDistance(it: Stations): Int {
        val stationCoordinates = it.getLatLng()
        val sharedPreferences = context?.getSharedPreferences("AA", AppCompatActivity.MODE_PRIVATE)
        val myLocationString = sharedPreferences!!.getString(MainActivity.MY_LAST_LOCATION, "")
        val myLocationArray = myLocationString.split(",")
        val myLocation = LatLng(myLocationArray[0].toDouble(), myLocationArray[1].toDouble())

        return  (stationCoordinates to myLocation).getDistance()
    }

}