package com.androidstrike.trackit.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Facility(
    val facilityName: String = "",
    val facilityEmail: String = "",
    val facilityAddress: String = "",
    val facilityAddressLongitude: String = "",
    val facilityAddressLatitude: String = "",
    val facilityPhoneNumber: String = "",
    val facilityId: String = "",
    val dateJoined: String = "",
    val role: String = "facility",
    val services: List<String> = listOf()
): Parcelable
