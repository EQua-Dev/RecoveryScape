//package com.androidstrike.trackit.services
//
//import android.app.Notification
//import android.app.NotificationManager
//import android.content.Context
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.core.app.NotificationCompat
//import com.androidstrike.trackit.R
//import com.androidstrike.trackit.utils.Common
//import com.androidstrike.trackit.utils.NotificationHelper
//import com.firebase.ui.auth.data.model.User
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import kotlin.random.Random
//
//class MyFirebaseMessagingService :FirebaseMessagingService() {
//
//    override fun onNewToken(s: String) {
//        super.onNewToken(s)
//
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user != null){
//            val tokens = FirebaseDatabase.getInstance().getReference(Common.TOKENS)
//            tokens.child(user!!.uid).setValue(s)
//        }
//    }
//
//    override fun onMessageReceived(p0: RemoteMessage) {
//        super.onMessageReceived(p0)
//        if (p0!!.data != null){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                sendNotificationwithChannel(p0)
//            else
//                sendNotification(p0)
//
//            addRequestToUserInformation(p0.data)
//        }
//    }
//
//    private fun sendNotification(p0: RemoteMessage) {
//        val data = p0.data
//        val title = "Friend Request"
//        val content = "New friend request from ${data[Common.FROM_EMAIL]!!}"
//
//        val builder = NotificationCompat.Builder(this,"")
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setContentTitle(title)
//            .setContentText(content)
//            .setAutoCancel(false)
//
//        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        manager.notify(java.util.Random().nextInt(), builder.build())
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun sendNotificationwithChannel(p0: RemoteMessage) {
//        val data = p0.data
//        val title = "Friend Request"
//        val content = "New friend request from ${data[Common.FROM_EMAIL]!!}"
//
//        val helper: NotificationHelper = NotificationHelper(this)
//        val builder: Notification.Builder = helper.getTrackitNotification(title, content)
//
//        helper.getManager()!!.notify(java.util.Random().nextInt(), builder.build())
//    }
//
//    private fun addRequestToUserInformation(data: Map<String, String>) {
//        //Pending Request
//        val friend_request = FirebaseDatabase.getInstance()
//            .getReference(Common.USER_INFORMATION)
//            .child(data[Common.TO_UID]!!)
//            .child(Common.FRIEND_REQUEST)
//
//        val user = com.androidstrike.trackit.model.User(data[Common.FROM_UID]!!, data[Common.FROM_EMAIL]!!)
//        friend_request.child(user.uid!!).setValue(user)
//
//    }
//}