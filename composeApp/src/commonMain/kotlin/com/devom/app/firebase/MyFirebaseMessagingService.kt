package com.devom.app.firebase

interface MyFirebaseMessaging {
    fun onNewToken(token: String)
    fun onMessageReceived(map: Map<String , String>)
}

expect object MyFirebaseMessagingService {
    var onNewNotification: () -> Unit
    fun getToken(onToken: (String , String) -> Unit)
}