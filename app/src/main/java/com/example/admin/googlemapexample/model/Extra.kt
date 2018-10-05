package com.example.admin.googlemapexample.model

class Extra {
    var uid: String? = null

    var zip: String? = null

    var districtCode: String? = null

    var status: String? = null

    var address: String? = null

    var nearbyStationList: Array<String>? = null

    override fun toString(): String {
        return "ClassPojo [uid = $uid, zip = $zip, districtCode = $districtCode, status = $status, address = $address, NearbyStationList = $nearbyStationList]"
    }
}