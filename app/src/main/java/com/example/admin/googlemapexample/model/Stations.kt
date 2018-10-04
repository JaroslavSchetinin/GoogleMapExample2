package com.example.admin.googlemapexample.model

import nl.qbusict.cupboard.annotation.Ignore

class Stations {

    var distanceToMe: Int = 0

    var timestamp: String? = null

    var id: String? = null

    var free_bikes: String? = null

    var extra: Extra? = null

    var name: String? = null

    var empty_slots: String? = null

    var longitude: String? = null

    var latitude: String? = null

    override fun toString(): String {
        return "ClassPojo [timestamp = $timestamp, id = $id, free_bikes = $free_bikes, extra = $extra, name = $name, empty_slots = $empty_slots, longitude = $longitude, latitude = $latitude]"
    }
}
