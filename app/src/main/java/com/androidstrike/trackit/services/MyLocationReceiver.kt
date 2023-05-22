//package com.androidstrike.trackit.services
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import com.androidstrike.trackit.utils.Common
//import com.google.android.gms.location.LocationResult
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import io.paperdb.Paper
//
//class MyLocationReceiver: BroadcastReceiver() {
//
//    lateinit var publicLocation: DatabaseReference
//    lateinit var uid: String
//
//    companion object{
//        val ACTION = "com.androidstrike.trackit.UPDATE_LOCATION"
//    }
//
//    init {
//        publicLocation = FirebaseDatabase.getInstance().getReference(Common.PUBLIC_LOCATION)
//    }
//
//    override fun onReceive(context: Context?, intent: Intent?) {
//        Paper.init(context)
//        uid = Paper.book().read(Common.USER_UID_SAVE_KEY)
//        if (intent != null){
//            val action = intent.action
//            if (action == ACTION){
//                val result = LocationResult.extractResult(intent)
//                if (result != null){
//                    val location = result.lastLocation
//                    if (Common.loggedUser != null){
//                        //app is running
//                        publicLocation.child(Common.loggedUser!!.uid!!).setValue(location)
//                    }
//                    else{
//                        //app is in killed mode
//                        publicLocation.child(uid).setValue(location)
//
//                    }
//                }
//            }
//        }
//
//    }
//}