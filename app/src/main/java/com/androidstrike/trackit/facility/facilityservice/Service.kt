package com.androidstrike.trackit.facility.facilityservice

data class Service(
    val serviceCategory: String = "",
    val servicePrice: String = "",
    val serviceDescription: String = "",
    val serviceDiscountedPrice: String = "",
    val serviceAvailablePlacesOption: String = "",
    val serviceStartEndDate: String = "",
    val serviceSchedule: String = "",
    val serviceID: String = "",
    val serviceOwnerID: String = "",
)
