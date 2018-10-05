package com.example.admin.googlemapexample


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.admin.googlemapexample.model.Stations

class StationRecyclerAdapter : RecyclerView.Adapter<RecyclerViewHolder>() {

    private var items: MutableList<Stations> = mutableListOf()

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