package com.devom.app.ui.screens.dashboard

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.devom.app.theme.backgroundColor
import com.devom.app.ui.components.BottomMenuBar
import com.devom.app.ui.components.BottomNavigationScreen
import com.devom.app.ui.navigation.Screens
import com.devom.app.ui.screens.home.HomeScreen
import com.devom.app.ui.screens.booking.BookingScreen
import com.devom.app.ui.screens.profile.ProfileScreen
import com.devom.app.ui.screens.wallet.WalletScreen
import com.devom.models.payment.GetWalletBalanceResponse
import com.devom.network.getUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_nav_add
import devom_app.composeapp.generated.resources.ic_nav_bookings
import devom_app.composeapp.generated.resources.ic_nav_home
import devom_app.composeapp.generated.resources.ic_nav_profile
import devom_app.composeapp.generated.resources.ic_nav_wallet

@Composable
fun DashboardScreen(appNavHostController: NavHostController) {
    val viewModel = viewModel { DashboardViewModel() }
    var selectedTab = viewModel.selectedTab.collectAsState().value
    val balance = viewModel.walletBalances.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val screens = listOf(
        BottomNavigationScreen("home", "Home", Res.drawable.ic_nav_home, false),
        BottomNavigationScreen("bookings", "Bookings", Res.drawable.ic_nav_bookings, false),
        BottomNavigationScreen("add", "Add", Res.drawable.ic_nav_add, false),
        BottomNavigationScreen("wallet", "Wallet", Res.drawable.ic_nav_wallet, false),
        BottomNavigationScreen("profile", "Profile", Res.drawable.ic_nav_profile, false),
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerContent(appNavHostController = appNavHostController, scope = scope, drawerState = drawerState, viewModel = viewModel, balance = balance) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Crossfade(
                targetState = selectedTab,
                modifier = Modifier.fillMaxSize().navigationBarsPadding().background(backgroundColor)
            ) { tab ->
                when (tab) {
                    0 -> HomeScreen(navHostController = appNavHostController) {
                        scope.launch {
                            drawerState.open()
                        }
                    }

                    1 -> BookingScreen(navHostController = appNavHostController) {
                        scope.launch {
                            drawerState.open()
                        }
                    }

                    3 -> WalletScreen(navHostController = appNavHostController) {
                        scope.launch {
                            drawerState.open()
                        }
                    }

                    4 -> ProfileScreen(navHostController = appNavHostController , onNavigationIconClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    })

                    else -> HomeScreen(navHostController = appNavHostController) {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                }
            }
            Box(
                modifier = Modifier.systemBarsPadding().fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                BottomMenuBar(
                    screens = screens,
                    selectedIndex = selectedTab,
                    onNavigateTo = {
                        if (it == 2) {
                            appNavHostController.navigate(Screens.UrgentBooking.path.plus("/false"))
                            return@BottomMenuBar
                        }
                        viewModel.onTabSelected(it)
                    },
                )
            }
        }
    }
}

@Composable
fun DrawerContent(
    appNavHostController: NavHostController,
    scope: CoroutineScope,
    drawerState: DrawerState,
    viewModel: DashboardViewModel,
    balance: State<GetWalletBalanceResponse>
) {
    NavigationDrawerContent(
        balance = balance,
        user = getUser(),
        appNavHostController = appNavHostController,
        onWalletClick = {
            viewModel.onTabSelected(3)
            scope.launch {
                drawerState.close()
            }
        },
        onBookings = {
            viewModel.onTabSelected(1)
            scope.launch {
                drawerState.close()
            }
        }, onDismiss = {
            scope.launch {
                drawerState.close()
            }
        }
    )
}
