package com.example.admin.googlemapexample

import com.google.gson.annotations.SerializedName

data class Station(var name: String,
                   var timestamp: String,
                   var longitude: Double,
                   var latitude: Double,
                   //@SerializedName("free_bikes")
                   //var freeBikes: Int,
                   //@SerializedName("empty_slots")
                   //var emptySlots: Int,
                   var id: String)