package com.example.admin.googlemapexample.fragment

import android.arch.lifecycle.ViewModelProviders
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
import com.example.admin.googlemapexample.MainActivity
import com.example.admin.googlemapexample.R
import com.example.admin.googlemapexample.StationListViewModel
import com.example.admin.googlemapexample.StationRecyclerAdapter
import com.example.admin.googlemapexample.extensions.getDistance
import com.example.admin.googlemapexample.extensions.getLatLng
import com.example.admin.googlemapexample.model.Station
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng

class StationListFragment : Fragment() {

    private val viewModel: StationListViewModel by lazy {
        ViewModelProviders.of(activity!!).get(StationListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.station_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val totalStationsText = view.findViewById<TextView>(R.id.total_stations_text_view)
        val totalBikesText = view.findViewById<TextView>(R.id.total_bikes_text_view)
        val totalSlotsText = view.findViewById<TextView>(R.id.total_slots_text_view)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = StationRecyclerAdapter()
        recyclerView.adapter = adapter

        val stations = viewModel.items ?: listOf()
        val totalStations = stations.size.toString()
        val totalBikes = stations.sumBy{it.free_bikes?.toInt() ?: 0}.toString()
        val totalSlots = stations.sumBy{it.empty_slots?.toInt() ?: 0}.toString()
        totalStationsText.text = totalStations
        totalBikesText.text = totalBikes
        totalSlotsText.text = totalSlots


        stations.forEach { it.distanceToMe = calculateDistance(it) }
        stations.let { adapter.setItems(it.sortedBy { it.distanceToMe }) }

        adapter.setOnRecyclerClicked(object : StationRecyclerAdapter.OnRecyclerClicked {
            override fun onClick(station: Station) {
                Toast.makeText(context, station.name, Toast.LENGTH_SHORT).show()
                fragmentManager?.popBackStack()
                MainActivity.map.animateCamera(CameraUpdateFactory.newLatLngZoom(station.latitude?.let { LatLng(it.toDouble(), station.longitude?.toDouble() ?: 0.0) }, 18f))
            }
        })
    }

    private fun calculateDistance(it: Station): Int {
        val stationCoordinates = it.getLatLng()
        val sharedPreferences = context?.getSharedPreferences("AA", AppCompatActivity.MODE_PRIVATE)
        val myLocationString = sharedPreferences?.getString(MainActivity.MY_LAST_LOCATION, "")
        val myLocationArray = myLocationString?.split(",")
        val myLat = myLocationArray?.get(0)?.toDouble() ?: 0.0
        val myLng = myLocationArray?.get(1)?.toDouble() ?: 0.0
        val myLocation = LatLng(myLat, myLng)

        return  (stationCoordinates to myLocation).getDistance()
    }
}