package com.androidstrike.trackit.remote

import com.androidstrike.trackit.model.MyResponse
import com.androidstrike.trackit.model.Request
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IFCMService {

    @Headers("Content-Type: application/json",
        "Authorization: key=AAAAOt2NfVU:APA91bGqTuo0CBYC55Bg4zPXhz46qOkUf9SIBXPNWW-oN0pJl1c0qkpg2zsulGoXTzBJmeXtgOu-_mz6wxQYlBg4_4pZo0Bb6wN6mZTOkiInWv-8cODq-ZvE-bg4Sem1YfqAxR_4V1T5")
    @POST("fcm/send")

    fun sendFriendRequestToUser(@Body body: Request): Observable<MyResponse>

}