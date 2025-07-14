package com.devom.app.firebase

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import co.touchlab.kermit.Logger
import com.devom.app.NOTIFICATION_PERMISSION_GRANTED
import com.devom.app.R
import com.devom.app.settings
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class AndroidFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Logger.d("FIREBASE_ACCESS_TOKEN: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.let {
           if (settings.getBoolean(NOTIFICATION_PERMISSION_GRANTED , false)) showNotification(it.title, it.body)
        }
    }

    private fun showNotification(title: String?, message: String?) {
        val builder =
            NotificationCompat.Builder(this, "devom_app_channel").setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(title ?: "Title").setContentText(message ?: "Message")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(0, builder.build())
    }

}

actual object MyFirebaseMessagingService {
    actual fun getToken(onToken: (String , String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    onToken(token , "ANDROID")
                }
        }
    }
}