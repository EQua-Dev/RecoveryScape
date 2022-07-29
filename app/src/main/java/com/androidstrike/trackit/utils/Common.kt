package com.androidstrike.trackit.utils

import com.androidstrike.trackit.model.User
import com.androidstrike.trackit.remote.IFCMService
import com.androidstrike.trackit.remote.RetrofitClient
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object Common {
    fun convertTimeStampToDate(time: Long): Date {
        return Date(Timestamp(time).time)
    }

    fun getDateFormatted(date: Date): String? {
        return SimpleDateFormat("dd-MM-yyyy HH:mm").format(date).toString()
    }

    val PUBLIC_LOCATION: String = "PublicLocation"
    val FRIEND_REQUEST: String = "FriendRequest"
    val ACCEPT_LIST: String = "acceptList"
    val USER_UID_SAVE_KEY: String = "SAVE_KEY"
    val TOKENS: String = "tokens"
    var loggedUser: User? = null
    var trackingUser: User? = null
    val USER_INFORMATION: String = "userInformation"

    val FROM_UID: String = "FromUid"
    val FROM_EMAIL: String = "FromName"
    val TO_UID: String = "ToUid"
    val TO_EMAIL: String = "ToName"

    val fcmService:IFCMService
    get() = RetrofitClient.getClient("https://fcm.googleapis.com/")
        .create(IFCMService::class.java)
}