package com.devom.app.firebase

actual object MyFirebaseMessagingService {
    actual fun getToken(onToken: (String , String) -> Unit) {
    }

    actual var onNewNotification: () -> Unit
        get() = TODO("Not yet implemented")
        set(value) {}
}