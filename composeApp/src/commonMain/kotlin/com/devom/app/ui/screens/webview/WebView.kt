package com.devom.app.ui.screens.webview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.devom.app.theme.backgroundColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.screens.wallet.WalletScreenContent
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_arrow_left
import devom_app.composeapp.generated.resources.my_wallet
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
expect fun WebView(
    url: String,
    modifier: Modifier = Modifier.fillMaxSize().background(backgroundColor),
)

@Composable
fun WebView(
    navHostController: NavHostController,
    url: String,
    modifier: Modifier = Modifier.fillMaxSize().background(backgroundColor),
) {
    Column(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            onNavigationIconClick = {
                navHostController.navigateUp()
            }
        )
        WebView(url = url, modifier = modifier)
    }
}