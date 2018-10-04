package com.example.admin.googlemapexample

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.admin.googlemapexample.MainActivity.Companion.MY_LAST_LOCATION
import com.example.admin.googlemapexample.model.Stations
import com.google.android.gms.maps.model.LatLng

class StationRecyclerAdapter() : RecyclerView.Adapter<RecyclerViewHolder>() {

    private var items: MutableList<Stations> = mutableListOf()
//    val sharedPreferences = context?.getSharedPreferences("AA", AppCompatActivity.MODE_PRIVATE)

    fun setItems(newItems: List<Stations>){
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_station_recycler, parent, false)
        return RecyclerViewHolder(view)

    }

    override fun getItemCount(): Int = items.size
//    private val myLocationString = sharedPreferences.getString(MY_LAST_LOCATION, "")
//    private val myLocationArray = myLocationString.split(",")
//    private val myLocation = if (myLocationString.isNotEmpty()) {
//        LatLng(myLocationArray[0].toDouble(), myLocationArray[1].toDouble())
//    } else null

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val station = items[position]

        holder.bind(station)
        holder.itemView.setOnClickListener {
            onRecyclerClicked?.onClick(station)
        }
    }

    private var onRecyclerClicked: OnRecyclerClicked? = null

    fun setOnRecyclerClicked(onRecyclerClicked: OnRecyclerClicked?){
        this.onRecyclerClicked = onRecyclerClicked
    }

    interface OnRecyclerClicked{
        fun onClick(station: Stations)
    }




}