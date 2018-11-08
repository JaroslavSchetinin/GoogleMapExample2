package com.example.admin.googlemapexample

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.ActivityCompat
import android.widget.TextView
import com.example.admin.googlemapexample.extensions.getDistance
import com.example.admin.googlemapexample.extensions.getLatLng
import com.example.admin.googlemapexample.fragment.BottomSheetFragment
import com.example.admin.googlemapexample.model.Station
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MyMapFragment : SupportMapFragment(), OnMapReadyCallback {

    companion object {
        lateinit var map: GoogleMap
    }

    private var lastLocation: LatLng? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private val viewModel: StationListViewModel by lazy {
        ViewModelProviders.of(activity!!).get(StationListViewModel::class.java)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        map.uiSettings.isZoomControlsEnabled = true
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        getLastLocation()
        map.uiSettings.isMyLocationButtonEnabled = true
        map.setOnMarkerClickListener { p0 ->
            fillOutTheForm(viewModel.items.second.firstOrNull { it.name == p0.title })
            BottomSheetFragment.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            false
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(activity as Activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity as Activity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MainActivity.LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            map.isMyLocationEnabled
            mFusedLocationClient?.lastLocation
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result != null) {
                            lastLocation = LatLng(task.result.latitude, task.result.longitude)
                            viewModel.myLastLocation = "${task.result.latitude}, ${task.result.longitude}"

                            lastLocation?.let {
                                map.addMarker(MarkerOptions().position(it)
                                        .title("My Position")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                            }
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(task.result?.latitude?.let { LatLng(it, task.result.longitude) }, 15f))
                        }
                    }
        }
    }

    private fun fillOutTheForm(station: Station?) {

        val freeBikesTextView = activity?.findViewById<TextView>(R.id.free_bikes_text_view)
        val emptySlotsTextView = activity?.findViewById<TextView>(R.id.empty_slots_text_view)
        val addressTextView = activity?.findViewById<TextView>(R.id.address_text_view)
        val navigateButton = activity?.findViewById<TextView>(R.id.navigate_button)
        val distanceTextView = activity?.findViewById<TextView>(R.id.distance_text_view)

        val freeBikes = " : ${station?.free_bikes}"
        val emptySlots = " : ${station?.empty_slots}"
        val stationAddress = "${station?.name?.replace("(PK)", "")?.toLowerCase()?.split(' ', '/')?.joinToString(" ") { it.capitalize() }}"
        val distance = if (lastLocation != null) {
            "${((lastLocation ?: LatLng(0.0, 0.0)) to (station?.getLatLng()
                    ?: LatLng(0.0, 0.0))).getDistance()} m."
        } else ""

        freeBikesTextView?.text = freeBikes
        emptySlotsTextView?.text = emptySlots
        addressTextView?.text = stationAddress
        distanceTextView?.text = distance
        navigateButton?.text = " Go Here"

        navigateButton?.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=${station?.latitude}, ${station?.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }
}
