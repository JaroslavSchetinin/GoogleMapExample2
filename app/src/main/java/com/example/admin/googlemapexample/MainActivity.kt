package com.example.admin.googlemapexample

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.admin.googlemapexample.model.Station
import com.google.android.gms.maps.*
import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.ActivityCompat
import android.widget.*
import com.example.admin.googlemapexample.extensions.*
import com.example.admin.googlemapexample.fragment.StationListFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val compositeDisposable = CompositeDisposable()
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: LatLng? = null

    private val viewModel: StationListViewModel by lazy {
        ViewModelProviders.of(this).get(StationListViewModel::class.java)
    }

    companion object {
        lateinit var map: GoogleMap
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        var bikeStations: Pair<String, List<Station>>? = null
        const val MY_LAST_LOCATION = "my_location"
        val timeStampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:SS", Locale.getDefault())
    }

    private val SHOW_ALL = 0
    private val SHOW_WITH_BIKES = 1
    private val SHOW_WITH_PARKING_SLOTS = 2

    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("AA", MODE_PRIVATE)
        sharedPreferences?.edit()?.putString(MY_LAST_LOCATION, "")?.apply()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.bottom_sheet_fragment_container, BottomSheetFragment())
                .commit()

        requestStations(SHOW_ALL)

        setupSearchButtons()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_list ->
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.recycler_view_fragment_container, StationListFragment())
                            .addToBackStack(null)
                            .commit()
                R.id.action_map -> {
                    if (supportFragmentManager.backStackEntryCount > 0)
                        supportFragmentManager.popBackStack()
                }
            }
            true
        }

        showStationsFromDatabase()
    }

    private fun showStationsFromDatabase() {
        viewModel.getStationsFromDatabase()
                ?.doOnSuccess { showAllStations(it) }
                ?.doOnError { it.message?.makeToast(this@MainActivity) }
                ?.subscribe()
    }

    private fun setupSearchButtons() {
        val lookingForBikeButton = findViewById<Button>(R.id.looking_for_a_bike)
        lookingForBikeButton.setOnClickListener {
            val differenceInMinutes = getDifferenceInMinutes()
            if (differenceInMinutes > 1)
                requestStations(SHOW_WITH_BIKES)
            else
                bikeStations?.second?.let { it1 -> showStationsWithBikes(it1) }
        }

        val lookingForParking = findViewById<Button>(R.id.looking_for_a_parking)
        lookingForParking.setOnClickListener {
            val differenceInMinutes = getDifferenceInMinutes()

            if (differenceInMinutes > 1)
                requestStations(SHOW_WITH_PARKING_SLOTS)
            else
                bikeStations?.second?.let { it1 -> showStationsWithParking(it1) }
        }

        val showAllStations = findViewById<Button>(R.id.show_all_stations)
        showAllStations.setOnClickListener {
            map.clear()
            val differenceInMinutes = getDifferenceInMinutes()
            if (differenceInMinutes > 1)
                requestStations(SHOW_ALL)
            else
                bikeStations?.second?.let { it1 -> showAllStations(it1) }
        }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun chooseIcon(it: Station): Int {
        val total = it.free_bikes?.toInt()?.let { it1 -> it.empty_slots?.toInt()?.plus(it1) }
        val coefOfFree: Double = total?.let { it1 -> it.free_bikes?.toDouble()?.div(it1) } ?: 0.0

        return when {
            coefOfFree == 0.0 -> R.drawable.loc_0
            0.0 < coefOfFree && coefOfFree <= 0.25 -> R.drawable.loc_25
            0.25 < coefOfFree && coefOfFree <= 0.5 -> R.drawable.loc_50
            0.5 < coefOfFree && coefOfFree <= 0.75 -> R.drawable.loc_75
            else -> R.drawable.loc_100
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            map.isMyLocationEnabled = true
            mFusedLocationClient?.lastLocation
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful && task.result != null) {
                            lastLocation = LatLng(task.result.latitude, task.result.longitude)
                            sharedPreferences?.edit()?.putString(MY_LAST_LOCATION, "${task.result.latitude}, ${task.result.longitude}")?.apply()

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

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        getLastLocation()
        map.setOnMarkerClickListener { p0 ->
            fillOutTheForm(bikeStations?.second?.firstOrNull { it.name == p0.title })
            BottomSheetFragment.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            false
        }
    }

    private fun fillOutTheForm(station: Station?) {

        val freeBikesTextView = findViewById<TextView>(R.id.free_bikes_text_view)
        val emptySlotsTextView = findViewById<TextView>(R.id.empty_slots_text_view)
        val addressTextView = findViewById<TextView>(R.id.address_text_view)
        val navigateButton = findViewById<TextView>(R.id.navigate_button)
        val distanceTextView = findViewById<TextView>(R.id.distance_text_view)

        val freeBikes = " : ${station?.free_bikes}"
        val emptySlots = " : ${station?.empty_slots}"
        val stationAddress = "${station?.name?.replace("(PK)", "")?.toLowerCase()?.split(' ', '/')?.joinToString(" ") { it.capitalize() }}"
        val distance = if (lastLocation != null) {
            "${((lastLocation ?: LatLng(0.0, 0.0)) to (station?.getLatLng()
                    ?: LatLng(0.0, 0.0))).getDistance()} m."
        } else ""

        freeBikesTextView.text = freeBikes
        emptySlotsTextView.text = emptySlots
        addressTextView.text = stationAddress
        distanceTextView.text = distance
        navigateButton.text = " Go Here"

        navigateButton.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=${station?.latitude}, ${station?.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }

    private fun requestStations(type: Int) {
        compositeDisposable.add(viewModel.getStationsFromWeb()
                .doOnSuccess { response ->
                    bikeStations = Calendar.getInstance().time.toTimestamp() to (response.network?.stations?.toList()
                            ?: listOf())
                    val bikesStationSecond = bikeStations?.second
                    doAsync { viewModel.stationsInsertion(bikesStationSecond) }
                    when (type) {
                        SHOW_WITH_BIKES -> bikesStationSecond?.let { showStationsWithBikes(it) }
                        SHOW_WITH_PARKING_SLOTS -> bikesStationSecond?.let { showStationsWithParking(it) }
                        else -> bikesStationSecond?.let { showAllStations(it) }
                    }
                }
                .subscribe())
    }

    private fun showStationsWithParking(second: List<Station>) {
        map.clear()
        second.filter { it.empty_slots?.toInt() ?: 0 > 0 }.forEach {
            map.addMarker(MarkerOptions().position(it.getLatLng()).title(it.name)
                    .icon(BitmapDescriptorFactory.fromResource(chooseIcon(it))))
        }
    }

    private fun showStationsWithBikes(second: List<Station>) {
        map.clear()
        second.filter { it.free_bikes?.toInt() ?: 0 > 0 }.forEach {
            map.addMarker(MarkerOptions().position(it.getLatLng()).title(it.name)
                    .icon(BitmapDescriptorFactory.fromResource(chooseIcon(it))))
        }
    }

    private fun showAllStations(second: List<Station>) {
        "count: ${second.size}".makeToast(this@MainActivity)
        second.forEach {
            map.addMarker(MarkerOptions().position(it.getLatLng()).title(it.name)
                    .icon(BitmapDescriptorFactory.fromResource(chooseIcon(it))))
        }
    }

    private fun getDifferenceInMinutes(): Int {
        return if (bikeStations?.first?.isEmpty() == true || bikeStations?.first == null) {
            0
        } else {
            val timeNow = Calendar.getInstance().time
            val timestampOfLastCall = bikeStations?.first?.toDate()
            return if (timestampOfLastCall != null) {
                getDifferenceBetweenDates(timeNow, timestampOfLastCall) / 60000
            } else 0
        }
    }

    private fun getDifferenceBetweenDates(timeNow: Date, timestampOfLastCall: Date): Int = timeNow.time.toInt() - timestampOfLastCall.time.toInt()
}
