package com.example.admin.googlemapexample.model

import android.location.Location

data class Network(
        val company: List<String>,
        val href: String,
        val id: String,
        val location: Location,
        val name: String,
        val stations: List<Station>
) {
    override fun toString(): String {
        return "ClassPojo [id = $id, stations = $stations, location = $location, name = $name, company = $company, href = $href]"
    }
}
