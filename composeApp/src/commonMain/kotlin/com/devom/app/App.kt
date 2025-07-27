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
import com.devom.Project
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.meenagopal24.sdk.PaymentSheet
import kotlin.coroutines.CoroutineContext

val settings = Settings()

@Composable
internal fun App() = AppTheme {
    val isLoggedIn by AuthManager.isLoggedIn.collectAsState()

    // Configure network only once when the user logs in
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            AuthManager.login(
                accessToken = AuthManager.accessToken.orEmpty(),
                refreshToken = AuthManager.refreshToken.orEmpty(),
                uuid = AuthManager.uuid.orEmpty()
            )
        }
    }

    MainScreen(isLoggedIn)
}


fun getLoginStatus(): Boolean {
    val accessKey = settings.get<String>(ACCESS_TOKEN_KEY)
    val uuid =  settings.get<String>(UUID_KEY)
    return accessKey.orEmpty().isNotEmpty() && uuid.orEmpty().isNotEmpty()
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


object AuthManager {
    private val settings = Settings()

    private val _isLoggedIn = MutableStateFlow(checkLoginStatus())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    val accessToken: String?
        get() = settings.get(ACCESS_TOKEN_KEY)

    val refreshToken: String?
        get() = settings.get(REFRESH_TOKEN_KEY)

    val uuid: String?
        get() = settings.get(UUID_KEY)
    init {
        configureNetwork()
    }

    fun login(accessToken: String, refreshToken: String, uuid: String) {
        settings.putString(ACCESS_TOKEN_KEY, accessToken)
        settings.putString(REFRESH_TOKEN_KEY, refreshToken)
        settings.putString(UUID_KEY, uuid)
        _isLoggedIn.value = true
        configureNetwork()
    }

    fun logout() {
        settings.remove(ACCESS_TOKEN_KEY)
        settings.remove(REFRESH_TOKEN_KEY)
        settings.remove(UUID_KEY)
        _isLoggedIn.value = false
        Project.other.clearCacheUseCase.invoke()
        Application.hideLoader()
    }

    private fun configureNetwork() {
        NetworkClient.configure {
            setTokens(access = accessToken.orEmpty(), refresh = refreshToken.orEmpty())
            baseUrl = BASE_URL
            onLogOut = {
                Logger.d("ON_LOGOUT") { "user has been logged out" }
                logout()
            }
            addHeaders {
                append(UUID_KEY, uuid.orEmpty())
                append(APPLICATION_ID , "com.devom.app")
            }
        }
    }

    private fun checkLoginStatus(): Boolean {
        return !accessToken.isNullOrEmpty() && !uuid.isNullOrEmpty()
    }
}
