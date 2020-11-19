package com.androidstrike.trackit.`interface`

interface IFirebaseLoadDone {
    fun onFirebaseUserDone (lstEmail:List<String>)
    fun onFirebaseLoadFailed(message: String)
}