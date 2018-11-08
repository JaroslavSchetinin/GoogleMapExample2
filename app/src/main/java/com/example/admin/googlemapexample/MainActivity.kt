package com.example.admin.googlemapexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.admin.googlemapexample.model.Station
import android.arch.lifecycle.ViewModelProviders
import android.support.design.widget.BottomNavigationView
import android.widget.Button
import com.example.admin.googlemapexample.extensions.*
import com.example.admin.googlemapexample.fragment.BottomSheetFragment
import com.example.admin.googlemapexample.fragment.StationListFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()
    private val emptyMarker = 0.0
    private val quarterMarker = 0.25
    private val halfMarker = 0.5
    private val threeQuartersMarker = 0.75

    private val viewModel: StationListViewModel by lazy {
        ViewModelProviders.of(this).get(StationListViewModel::class.java)
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val MY_LAST_LOCATION = "my_location"
        val timeStampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:SS", Locale.getDefault())
    }

    private val SHOW_ALL = 0
    private val SHOW_WITH_BIKES = 1
    private val SHOW_WITH_PARKING_SLOTS = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.setMyLastLocationInPrefs("")
        viewModel.myLastLocation = ""

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MyMapFragment
        mapFragment.getMapAsync(mapFragment)

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
                    if (supportFragmentManager.backStackEntryCount > 0){
                        supportFragmentManager.popBackStack()
                    }
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
            if (differenceInMinutes > 1) {
                requestStations(SHOW_WITH_BIKES)
            } else {
                showStationsWithBikes(viewModel.items.second)
            }
        }

        val lookingForParking = findViewById<Button>(R.id.looking_for_a_parking)
        lookingForParking.setOnClickListener {
            val differenceInMinutes = getDifferenceInMinutes()

            if (differenceInMinutes > 1) {
                requestStations(SHOW_WITH_PARKING_SLOTS)
            } else {
                showStationsWithParking(viewModel.items.second)
            }
        }

        val showAllStations = findViewById<Button>(R.id.show_all_stations)
        showAllStations.setOnClickListener {
            MyMapFragment.map.clear()
            val differenceInMinutes = getDifferenceInMinutes()
            if (differenceInMinutes > 1) {
                requestStations(SHOW_ALL)
            } else {
                showAllStations(viewModel.items.second)
            }
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
            coefOfFree == emptyMarker -> R.drawable.loc_0
            emptyMarker < coefOfFree && coefOfFree <= quarterMarker -> R.drawable.loc_25
            quarterMarker < coefOfFree && coefOfFree <= halfMarker -> R.drawable.loc_50
            halfMarker < coefOfFree && coefOfFree <= threeQuartersMarker -> R.drawable.loc_75
            else -> R.drawable.loc_100
        }
    }

    private fun requestStations(type: Int) {
        compositeDisposable.add(viewModel.getStationsFromWeb()
                .doOnSuccess { response ->
                    val bikesStationSecond = response.network.stations.toList()
                    doAsync { viewModel.stationsInsertion(response.network.stations.toList()) }
                    when (type) {
                        SHOW_WITH_BIKES -> showStationsWithBikes(bikesStationSecond)
                        SHOW_WITH_PARKING_SLOTS -> showStationsWithParking(bikesStationSecond)
                        else -> showAllStations(bikesStationSecond)
                    }
                }
                .subscribe())
    }

    private fun showStationsWithParking(second: List<Station>) {
        MyMapFragment.map.clear()
        second.filter { it.empty_slots?.toInt() ?: 0 > 0 }.forEach {
            MyMapFragment.map.addMarker(MarkerOptions().position(it.getLatLng()).title(it.name)
                    .icon(BitmapDescriptorFactory.fromResource(chooseIcon(it))))
        }
    }

    private fun showStationsWithBikes(second: List<Station>) {
        MyMapFragment.map.clear()
        second.filter { it.free_bikes?.toInt() ?: 0 > 0 }.forEach {
            MyMapFragment.map.addMarker(MarkerOptions().position(it.getLatLng()).title(it.name)
                    .icon(BitmapDescriptorFactory.fromResource(chooseIcon(it))))
        }
    }

    private fun showAllStations(second: List<Station>) {
        "count: ${second.size}".makeToast(this@MainActivity)
        second.forEach {
            MyMapFragment.map.addMarker(MarkerOptions().position(it.getLatLng()).title(it.name)
                    .icon(BitmapDescriptorFactory.fromResource(chooseIcon(it))))
        }
    }

    private fun getDifferenceInMinutes(): Int {
        return if (viewModel.items.first.isEmpty()) {
            0
        } else {
            val timeNow = Calendar.getInstance().time
            val timestampOfLastCall = viewModel.items.first.toDate()
            return getDifferenceBetweenDates(timeNow, timestampOfLastCall) / 60000
        }
    }

    private fun getDifferenceBetweenDates(timeNow: Date, timestampOfLastCall: Date): Int = timeNow.time.toInt() - timestampOfLastCall.time.toInt()
}