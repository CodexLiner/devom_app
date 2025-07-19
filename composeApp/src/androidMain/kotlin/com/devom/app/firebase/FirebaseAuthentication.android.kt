package com.devom.app.firebase

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.devom.models.auth.GoogleSignInRequest
import com.devom.utils.Application
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
object FirebaseAuthenticationManager {
    private lateinit var activity: Activity
    private val scope = CoroutineScope(Dispatchers.Main)

    fun init(activity: Activity) {
        this.activity = activity
    }

    fun initiateGoogleSignIn(onSignInSuccess: (GoogleSignInRequest) -> Unit = {}) {
        if (!::activity.isInitialized) {
            throw IllegalStateException("FirebaseAuthenticationManager is not initialized. Call init(activity) first.")
        }
        doGoogleSignIn(onSignInSuccess)
    }

    private fun doGoogleSignIn(onSignInSuccess: (GoogleSignInRequest) -> Unit = {}) {
        val credentialManager = CredentialManager.create(activity)

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(getCredentialOptions())
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                when (val credential = result.credential) {
                    is CustomCredential -> {
                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val googleCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)

                            val idToken = googleCredential.idToken
                            val displayName = googleCredential.displayName
                            val email = googleCredential.id
                            val givenName = googleCredential.givenName
                            val familyName = googleCredential.familyName
                            val profilePictureUri = googleCredential.profilePictureUri
                            val firebaseAuthentication = GoogleSignInRequest(
                                token = idToken,
                                email = email,
                                name = displayName.orEmpty(),
                                firstName = givenName.orEmpty(),
                                lastName = familyName.orEmpty(),
                                photoUrl = profilePictureUri.toString()
                            )
                            onSignInSuccess(firebaseAuthentication)
                        }

                    }

                    else -> {
                        Log.w("GoogleSignIn", "Unhandled credential type: ${credential.javaClass}")
                    }
                }
            } catch (e: NoCredentialException) {
                Application.hideLoader()
                Log.w("GoogleSignIn", "No credentials found", e)
            } catch (e: GetCredentialException) {
                Application.hideLoader()
                Log.e("GoogleSignIn", "Error getting credential", e)
            }
        }
    }

    private fun getCredentialOptions(): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .setServerClientId("1084106475388-q05rpt3a8s78nqdubjorbiref5b5mv29.apps.googleusercontent.com")
            .build()
    }
}

actual fun initiateGoogleSignIn(onSignInSuccess: (GoogleSignInRequest) -> Unit) {
    Application.showLoader()
    FirebaseAuthenticationManager.initiateGoogleSignIn(onSignInSuccess)
}