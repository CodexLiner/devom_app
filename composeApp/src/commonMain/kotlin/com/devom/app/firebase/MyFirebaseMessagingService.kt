package com.devom.app.firebase

interface MyFirebaseMessaging {
    fun onNewToken(token: String)
    fun onMessageReceived(map: Map<String , String>)
}

expect object MyFirebaseMessagingService {
    fun getToken(onToken: (String , String) -> Unit)
}