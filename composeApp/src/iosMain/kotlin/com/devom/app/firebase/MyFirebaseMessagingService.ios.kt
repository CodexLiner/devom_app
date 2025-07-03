package com.devom.app.firebase

actual object MyFirebaseMessagingService {
    actual fun getToken(onToken: (String , String) -> Unit) {
    }
}