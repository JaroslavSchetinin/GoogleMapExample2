package com.example.admin.googlemapexample.model

import android.location.Location

class Network {
    var id: String? = null

    var stations: Array<Station>? = null

    var location: Location? = null

    var name: String? = null

    var company: Array<String>? = null

    var href: String? = null

    override fun toString(): String {
        return "ClassPojo [id = $id, stations = $stations, location = $location, name = $name, company = $company, href = $href]"
    }
}
