package com.androidstrike.trackit.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.androidstrike.trackit.R

class NotificationHelper(base: Context): ContextWrapper(base) {

    companion object{
        private val TRACKIT_CHANNEL_ID = "com.androidstrike.trackit"
        private val TRACKIT_CHANNEL_NAME = "Trackit"
    }

    private var manager:NotificationManager?=null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(defaultUri: Uri?) {
        val trackitChannel = NotificationChannel(TRACKIT_CHANNEL_ID, TRACKIT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)

        trackitChannel.enableLights(true)
        trackitChannel.enableVibration(true)

        trackitChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
            .build()

        trackitChannel.setSound(defaultUri!!, audioAttributes)

        getManager()!!.createNotificationChannel(trackitChannel)

    }

    public fun getManager(): NotificationManager{
        if (manager == null)
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return manager!!
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTrackitNotification(title:String, content:String): Notification.Builder{
        return Notification.Builder(applicationContext, TRACKIT_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(false)
    }
}