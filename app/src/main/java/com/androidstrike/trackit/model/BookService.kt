package com.androidstrike.trackit.model

data class BookService(
    val selectedAppointmentService: String = "",
    val selectedAppointmentDate: String = "",
    val selectedAppointmentTime: String = "",
    val selectedAppointmentDescription: String = "",
    val dateBooked: String = "",
    val clientId: String = "",
    val facilityId: String = "",
    val status: String = ""
)
