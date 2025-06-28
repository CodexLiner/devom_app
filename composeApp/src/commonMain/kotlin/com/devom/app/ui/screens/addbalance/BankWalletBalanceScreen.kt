package com.devom.app.ui.screens.addbalance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.bgColor
import com.devom.app.theme.blackColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.text_style_h4
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.TextInputField
import com.devom.app.ui.screens.wallet.WalletBalanceInfo
import com.devom.app.ui.screens.wallet.WalletIcon
import com.devom.utils.Application
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_arrow_left
import org.jetbrains.compose.resources.painterResource

@Composable
fun BankWalletBalanceScreen(navController: NavController) {
    val viewModel: BankWalletViewModel = viewModel { BankWalletViewModel() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .imePadding()
    ) {
        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = "Add Cash",
            onNavigationIconClick = { navController.popBackStack() }
        )

        BankWalletScreenContent(navController, viewModel)
    }
}

@Composable
fun ColumnScope.BankWalletScreenContent(
    navController: NavController,
    viewModel: BankWalletViewModel,
) {
    val walletBalance by viewModel.walletBalance.collectAsState()
    var amount by remember { mutableStateOf("") }

    val cornerShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
    val balance = (walletBalance?.cashWallet?.toFloatOrNull() ?: 0f) +
            (walletBalance?.bonusWallet?.toFloatOrNull() ?: 0f)

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, cornerShape)
            .border(1.dp, whiteColor, cornerShape)
            .padding(16.dp)
    ) {
        WalletIcon()
        WalletBalanceInfo()
        Text(
            text = "₹${balance}",
            color = blackColor,
            style = text_style_h4
        )
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp)
            .weight(1f)
    ) {
        Text(
            text = "Enter Amount",
            color = blackColor,
            fontWeight = FontWeight.W500,
            fontSize = 16.sp
        )

        TextInputField(
            initialValue = amount,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            leadingIcon = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .background(bgColor, RoundedCornerShape(10.dp))
                        .size(48.dp),
                ) {
                    Text(
                        text = "₹",
                        color = blackColor,
                        fontWeight = FontWeight.W500,
                        fontSize = 16.sp
                    )
                }
            },
            backgroundColor = whiteColor,
            placeholder = "Enter Amount",
            modifier = Modifier
                .padding(top = 4.dp)
                .border(1.dp, greyColor, RoundedCornerShape(12.dp))
        ) {
            amount = it
        }

        val quickAmounts = listOf(500, 750, 1000, 1500, 2000)
        LazyRow(
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(quickAmounts) { value ->
                Text(
                    text = "₹$value",
                    modifier = Modifier
                        .border(1.dp, greyColor.copy(alpha = 0.36f), RoundedCornerShape(15.dp))
                        .clickable {
                            amount = (amount.toIntOrNull() ?: 0).plus(value).toString()
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }

    ButtonPrimary(
        buttonText = "Add",
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 48.dp)
            .fillMaxWidth()
            .height(58.dp),
        onClick = {
            val enteredAmount = amount.toIntOrNull()
            if (enteredAmount != null && enteredAmount > 0) {
                viewModel.addWalletBalance(enteredAmount)
            } else {
                Application.showToast("please enter valid amount")
            }
        }
    )
}
