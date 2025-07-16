package com.devom.app

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
import com.devom.app.firebase.MyFirebaseMessagingService
import com.devom.app.theme.AppTheme
import com.devom.app.ui.components.AppContainer
import com.devom.app.ui.components.ProgressLoader
import com.devom.app.ui.components.ShowSnackBar
import com.devom.app.ui.navigation.NavigationHost
import com.devom.app.ui.navigation.Screens
import com.devom.app.ui.providers.LoadingCompositionProvider
import com.devom.network.NetworkClient
import com.devom.utils.Application
import com.devom.utils.Application.isLoggedIn
import com.devom.utils.Application.loaderState
import com.devom.utils.Application.loginState
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import me.meenagopal24.sdk.PaymentSheet

val settings = Settings()

@Composable
internal fun App() = AppTheme {
    var accessKey by remember { mutableStateOf(settings.get<String>(ACCESS_TOKEN_KEY)) }
    var refreshToken by remember { mutableStateOf(settings.get<String>(ACCESS_TOKEN_KEY)) }
    var uuid by remember { mutableStateOf(settings.get<String>(UUID_KEY)) }
    PaymentSheet.setApiKey("rzp_test_Zj1CPzIAHZ4lwN")

    val isLoggedIn by loginState.collectAsState()
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            listOf(ACCESS_TOKEN_KEY, UUID_KEY).forEach { settings.remove(it) }
        } else {
            accessKey = settings.get(ACCESS_TOKEN_KEY)
            refreshToken = settings.get(ACCESS_TOKEN_KEY)
            uuid = settings.get(UUID_KEY)
        }

        val loggedIn = listOf(accessKey, refreshToken, uuid).all { !it.isNullOrEmpty() }
        isLoggedIn(loggedIn)

        NetworkClient.configure {
            setTokens(access = accessKey.orEmpty(), refresh = refreshToken.orEmpty())
            baseUrl = BASE_URL
            onLogOut = {
                Logger.d("ON_LOGOUT") { "user has been logged out" }
                Application.hideLoader()
                isLoggedIn(false)
            }
            addHeaders {
                append(UUID_KEY, uuid.orEmpty())
                append(APPLICATION_ID , "com.devom.app")

            }
        }

        initialized = true
    }

    if (initialized) {
        MainScreen(isLoggedIn)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    val loader by loaderState.collectAsStateWithLifecycle()

    LoadingCompositionProvider(state = loader) {
        AppContainer {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { ShowSnackBar() }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    NavigationHost(
                        navController = navController,
                        startDestination = if (isLoggedIn) Screens.Dashboard.path else Screens.Login.path
                    )
                    ProgressLoader()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        MyFirebaseMessagingService.getToken { token , _ ->
            Logger.d("FIREBASE_ACCESS_TOKEN :- $token")
        }
    }
}
