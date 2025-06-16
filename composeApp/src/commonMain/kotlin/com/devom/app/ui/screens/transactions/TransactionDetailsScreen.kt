package com.devom.app.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devom.app.theme.backgroundColor
import com.devom.app.ui.components.AppBar
import com.devom.models.payment.WalletTransaction
import org.jetbrains.compose.resources.painterResource
import pandijtapp.composeapp.generated.resources.Res
import pandijtapp.composeapp.generated.resources.ic_arrow_left

@Composable
fun TransactionDetailsScreen(
    screenTitle: String = "",
    navController: NavController,
    transaction: WalletTransaction? = null,
) {
    val viewModel: TransactionsScreenViewModel = viewModel {
        TransactionsScreenViewModel()
    }
    Column(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = screenTitle,
            onNavigationIconClick = { navController.popBackStack() }
        )
        TransactionDetailsScreenContent()
    }
}

@Composable
fun TransactionDetailsScreenContent() {
    TODO("Not yet implemented")
}