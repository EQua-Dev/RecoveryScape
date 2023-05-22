package com.androidstrike.trackit.model

data class Client(
    val userId: String = "",
    val userFirstName: String = "",
    val userLastName: String = "",
    val userEmail: String = "",
    val userAddressLongitude: String = "",
    val userAddressLatitude: String = "",
    val dateJoined: String = "",
    val role: String = "client"
)
